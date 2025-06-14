resource "aws_ecs_cluster" "dev" {
  name = var.cluster_name

  setting {
    name  = var.cluster_settings.name
    value = var.cluster_settings.value
  }

  tags = merge(var.tags, {
    Cluster = var.cluster_name
  })
}