locals {
  cluster_name        = "${var.environment}-cluster"
  launch_type         = "EC2"
  scheduling_strategy = "DAEMON"

  settings = {
    name  = "containerInsights"
    value = "disabled"
  }

  deployment_controller_type = "ECS"
}
