output "ecr_repository_name" {
  value = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names["prod"]
}

output "ecs_cluster_name" {
  value = module.ecs.cluster_name
}

output "ecs_api_service_name" {
  value = module.ecs.service_names["api-prod"]
}

output "ecs_api_container_name" {
  value = module.ecs.container_names["api-prod"]
}

output "rds_endpoint" {
  description = "RDS endpoint address"
  value       = module.rds.endpoint
}

output "rds_arn" {
  description = "RDS instance ARN"
  value       = module.rds.arn
}
