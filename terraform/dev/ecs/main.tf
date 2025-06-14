module "cluster" {
  source           = "./cluster"
  cluster_name     = local.cluster_name
  cluster_settings = local.settings
  tags             = var.tags
}

module "service" {
  source                     = "./service"
  cluster_id                 = module.cluster.cluster_id
  deployment_controller_type = local.deployment_controller_type
  ecs_services               = var.ecs_services
  launch_type                = local.launch_type
  scheduling_strategy        = local.scheduling_strategy
  task_definition_arn        = module.task.task_definition_arns
  alb_target_group_arns      = var.alb_target_group_arns
  tags                       = var.tags
}

module "task" {
  source                    = "./task"
  container_definitions_map = local.container_definitions_map
  ecs_task_definitions      = var.ecs_task_definitions
  tags                      = var.tags
}