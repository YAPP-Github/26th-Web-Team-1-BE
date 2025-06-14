resource "aws_alb" "common" {
  name                       = local.alb_name
  internal                   = local.internal
  load_balancer_type         = local.loadbalancer_type
  security_groups            = var.alb_security_group_id
  subnets                    = var.subnets
  enable_deletion_protection = local.deletion_protection
  drop_invalid_header_fields = true

  tags = local.alb_tags
}

module "http" {
  source        = "./http-listener"
  alb_arn       = aws_alb.common.arn
  http_listener = local.http_listener
}

module "https" {
  source         = "./https-listener"
  alb_arn        = aws_alb.common.arn
  https_listener = local.https_listener
  listener_rules = local.listener_rules
  depends_on = [var.certificate_validation_complete]
}

module "target_groups" {
  source        = "./target-group"
  target_groups = local.target_groups
  vpc_id        = var.vpc_id
}