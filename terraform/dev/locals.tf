data "terraform_remote_state" "bootstrap" {
  backend = "s3"

  config = {
    bucket = "eatda-tf-state"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

data "terraform_remote_state" "common" {
  backend = "s3"
  config = {
    bucket = "eatda-tf-state"
    key    = "common/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

locals {
  project_name = "eatda"
  region       = "ap-northeast-2"
  environment  = "dev"
  name_prefix  = "eatda"

  ec2_sg_id             = data.terraform_remote_state.common.outputs.security_group_ids["ec2"]
  instance_subnet_map = data.terraform_remote_state.common.outputs.public_subnet_ids # 에러가 났던 부분
  ecr_repo_urls         = data.terraform_remote_state.bootstrap.outputs.ecr_repo_urls
  ecs_services          = var.ecs_services
  alb_target_group_arns = data.terraform_remote_state.common.outputs.target_group_arns

  common_tags = {
    Project   = local.project_name
    ManagedBy = "terraform"
  }

  dev_instance_definitions = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t2.micro"
    role                 = "dev"
    iam_instance_profile = data.terraform_remote_state.common.outputs.instance_profile_name["ec2-to-ecs"]
    key_name             = "eatda-ec2-dev-key"
    user_data            = <<-EOF
#!/bin/bash
echo ECS_CLUSTER=dev-cluster >> /etc/ecs/ecs.config
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
EOF
  }

  task_definitions_with_roles = {
    for k, v in var.ecs_task_definitions_base :
    k => merge(
      v,
      {
        execution_role_arn = data.terraform_remote_state.common.outputs.role_arn["ecsTaskExecutionRole"],
        task_role_arn      = data.terraform_remote_state.common.outputs.role_arn["ecsAppTaskRole"]
      }
    )
  }

  container_definitions_map = {
    for svc, def in local.task_definitions_with_roles : svc => [
      {
        name         = svc
        image        = svc == "api-dev" ? "${local.ecr_repo_urls["dev"]}:placeholder" : def.container_image
        cpu          = def.cpu
        memory       = def.memory
        essential    = true
        stopTimeout = lookup(def, "stop_timeout", 30)
        command      = svc == "api-dev" ? ["java", "-Dspring.profiles.active=dev", "-jar", "/api.jar"] : null
        portMappings = [
          for idx, port in def.container_port :
          { name = "${svc}-${port}-tcp", containerPort = port, hostPort = def.host_port[idx], protocol = "tcp" }
        ]
        environment = [for k, v in lookup(def, "environment", {}) : { name = k, value = v }]
        secrets     = svc == "mysql-dev" ? [
          { name = "MYSQL_USER", valueFrom = "/dev/MYSQL_USER_NAME" },
          { name = "MYSQL_ROOT_PASSWORD", valueFrom = "/dev/MYSQL_ROOT_PASSWORD" },
          { name = "MYSQL_PASSWORD", valueFrom = "/dev/MYSQL_PASSWORD" }
        ] : [
          for s in lookup(def, "secrets", []) : {
            name      = s.name
            valueFrom = s.valueFrom
          }
        ]
        mountPoints = [
          for vol in lookup(def, "volumes", []) :{
            sourceVolume = vol.name, containerPath = lookup(var.volume_mount_paths, vol.name, "/eatda"),
            readOnly     = false
          }
        ]
      }
    ]
  }

  final_ecs_definitions_for_module = {
    for key, task_def in local.task_definitions_with_roles : key => merge(
      task_def,
      {
        container_definitions = local.container_definitions_map[key]
      }
    )
  }
}
