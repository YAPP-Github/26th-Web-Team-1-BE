resource "aws_ecs_task_definition" "dev" {
  for_each = var.ecs_task_definitions

  family                   = each.key
  network_mode             = each.value.network_mode
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  task_role_arn            = each.value.task_role_arn
  execution_role_arn       = each.value.execution_role_arn
  requires_compatibilities = each.value.requires_compatibilities

  container_definitions = jsonencode(each.value.container_definitions)

  dynamic "volume" {
    for_each = each.value.volumes
    content {
      name      = volume.value.name
      host_path = volume.value.host_path
    }
  }

  tags = merge(var.tags, {
    Task = each.key
  })
}
