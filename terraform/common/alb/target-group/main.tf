resource "aws_lb_target_group" "common" {
  for_each = var.target_groups

  name        = each.key
  port        = each.value.port
  protocol    = each.value.protocol
  target_type = each.value.target_type

  vpc_id = var.vpc_id

  health_check {
    path                = each.value.health_check.path
    protocol            = each.value.health_check.protocol
    interval            = each.value.health_check.interval
    matcher             = each.value.health_check.matcher
    healthy_threshold   = each.value.health_check.healthy_threshold
    unhealthy_threshold = each.value.health_check.unhealthy_threshold
  }
}
