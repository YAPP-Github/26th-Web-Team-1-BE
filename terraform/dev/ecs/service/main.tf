resource "aws_ecs_service" "dev" {
  for_each = var.ecs_services

  name            = each.key
  cluster         = var.cluster_id
  launch_type = lookup(each.value, "launch_type", var.launch_type)
  task_definition = var.task_definition_arn[each.key]
  desired_count   = each.value.desired_count
  scheduling_strategy = lookup(each.value, "scheduling_strategy", var.scheduling_strategy)

  dynamic "load_balancer" {
    for_each = try(each.value.load_balancer, null) != null ? [each.value.load_balancer] : []
    content {
      target_group_arn = var.alb_target_group_arns[load_balancer.value.target_group_key]
      container_name   = load_balancer.value.container_name
      container_port   = load_balancer.value.container_port
    }
  }

  deployment_controller {
    type = var.deployment_controller_type
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}