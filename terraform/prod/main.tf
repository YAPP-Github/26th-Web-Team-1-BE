data "aws_ssm_parameter" "rds_user_name" {
  name            = "/prod/MYSQL_USER_NAME"
  with_decryption = true
}

data "aws_ssm_parameter" "rds_password" {
  name            = "/prod/MYSQL_PASSWORD"
  with_decryption = true
}

module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = local.ec2_sg_id
  instance_definitions = local.prod_instance_definitions
  instance_subnet_map  = local.instance_subnet_map
  name_prefix          = local.name_prefix
  tags                 = local.common_tags
}

module "ecs" {
  source                = "./ecs"
  alb_target_group_arns = local.alb_target_group_arns
  ecr_repo_urls         = local.ecr_repo_urls
  ecs_services          = var.ecs_services
  ecs_task_definitions  = local.ecs_task_definitions
  environment           = local.environment
  tags                  = local.common_tags
}

module "rds" {
  source                  = "./rds"
  vpc_id                  = local.vpc_id
  vpc_cidr                = local.vpc_cidr
  availability_zones      = local.availability_zones
  identifier              = local.identifier
  instance_class          = local.instance_class
  engine                  = local.engine
  engine_version          = local.engine_version
  allocated_storage       = local.allocated_storage
  username                = data.aws_ssm_parameter.rds_user_name.value
  password                = data.aws_ssm_parameter.rds_password.value
  vpc_security_group_ids  = local.vpc_security_group_ids
  multi_az                = false
  backup_retention_period = 0
  storage_encrypted       = true
  tags                    = local.common_tags
}
