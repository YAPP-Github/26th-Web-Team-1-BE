output "ecs_service_names" {
  value = {
    for k, v in aws_ecs_service.dev : k => v.name
  }
}
