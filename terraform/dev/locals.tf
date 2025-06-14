data "terraform_remote_state" "bootstrap" {
  backend = "s3"

  config = {
    bucket = "timeeat-tf-state"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

data "terraform_remote_state" "common" {
  backend = "s3"
  config = {
    bucket = "timeeat-tf-state"
    key    = "common/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

locals {
  project_name = "time-eat"
  region       = "ap-northeast-2"
  environment  = "dev"
  name_prefix  = "time-eat"

  ec2_sg_id                 = data.terraform_remote_state.common.outputs.security_group_ids["ec2"]
  instance_definitions      = data.terraform_remote_state.common.outputs.instance_profile_name["ec2-to-ecs"]
  instance_subnet_map       = data.terraform_remote_state.common.outputs.public_subnet_ids
  ecr_repo_names            = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names
  ecs_services              = var.ecs_services
  ecs_task_definitions_base = var.ecs_task_definitions_base
  alb_target_group_arns     = data.terraform_remote_state.common.outputs.target_group_arns

  common_tags = {
    Project   = local.project_name
    ManagedBy = "terraform"
  }
}

locals {
  dev_instance_definitions = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t2.micro"
    role                 = "dev"
    iam_instance_profile = "ec2-to-ecs"
    key_name             = "time-eat-ec2-dev-key"
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
}

locals {
  ecs_task_definitions = {
    for k, v in var.ecs_task_definitions_base :
    k => merge(
      v,
      {
        execution_role_arn = data.terraform_remote_state.common.outputs.role_arn["ecsTaskExecutionRole"],
        task_role_arn      = data.terraform_remote_state.common.outputs.role_arn["ecsAppTaskRole"]
      },
        k == "api-dev" ? {
        container_image = "${data.terraform_remote_state.bootstrap.outputs.ecr_repo_names["dev"]}:latest"
      } : {},
    )
  }
}
