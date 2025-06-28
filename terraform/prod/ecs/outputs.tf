output "cluster_name" {
  value = module.cluster.cluster_name
}

output "service_names" {
  value = module.service.ecs_service_names
}

output "container_names" {
  value = module.task.container_names
}
