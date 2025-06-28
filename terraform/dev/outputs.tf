output "ecs_task_definitions_check" {
  value = local.final_ecs_definitions_for_module
}

output "ecr_repository_name" {
  value = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names["dev"]
}

output "ecs_cluster_name" {
  value = module.ecs.cluster_name
}

output "ecs_api_service_name" {
  value = module.ecs.service_names["api-dev"]
}

output "ecs_api_container_name" {
  value = module.ecs.container_names["api-dev"]
}

output "ec2_private_ip" {
  value = module.ec2.private_ip
}
