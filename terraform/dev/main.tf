module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = local.ec2_sg_id
  instance_definitions = local.dev_instance_definitions
  instance_subnet_map  = local.instance_subnet_map
  name_prefix          = local.name_prefix
  tags                 = local.common_tags
}

module "ecs" {
  source                = "./ecs"
  alb_target_group_arns = local.alb_target_group_arns
  ecr_repo_urls         = local.ecr_repo_urls
  ecs_services          = var.ecs_services
  ecs_task_definitions  = local.final_ecs_definitions_for_module
  environment           = local.environment
  tags                  = local.common_tags
}
