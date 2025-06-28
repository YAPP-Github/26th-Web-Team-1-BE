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
  environment  = "prod"
  name_prefix  = "eatda"

  ec2_sg_id                 = data.terraform_remote_state.common.outputs.security_group_ids["ec2"]
  instance_definitions      = data.terraform_remote_state.common.outputs.instance_profile_name["ec2-to-ecs"]
  instance_subnet_map       = data.terraform_remote_state.common.outputs.public_subnet_ids
  ecr_repo_urls             = data.terraform_remote_state.bootstrap.outputs.ecr_repo_urls
  ecs_services              = var.ecs_services
  ecs_task_definitions_base = var.ecs_task_definitions_base
  alb_target_group_arns     = data.terraform_remote_state.common.outputs.target_group_arns
  vpc_id                    = data.terraform_remote_state.common.outputs.vpc_id
  vpc_cidr                  = data.terraform_remote_state.common.outputs.vpc_cidr_block
  availability_zones        = data.terraform_remote_state.common.outputs.availability_zones
  vpc_security_group_ids = [data.terraform_remote_state.common.outputs.security_group_ids["rds"]]

  common_tags = {
    Project   = local.project_name
    ManagedBy = "terraform"
  }
}

locals {
  prod_instance_definitions = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t3.micro"
    role                 = "prod"
    iam_instance_profile = "ec2-to-ecs"
    key_name             = "eatda-ec2-prod-key"
    user_data            = <<-EOF
#!/bin/bash
echo ECS_CLUSTER=prod-cluster >> /etc/ecs/ecs.config

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
        k == "api-prod" ? {
        container_image = "${data.terraform_remote_state.bootstrap.outputs.ecr_repo_urls["prod"]}:placeholder"
      } : {},
    )
  }
}

locals {
  allocated_storage = 20
  identifier        = "${local.project_name}-${local.environment}-db"
  instance_class    = "db.t3.micro"
  engine_version    = "8.0.42"
  engine            = "MySQL"
  storage_encrypted = true
  multi_az          = false
}
