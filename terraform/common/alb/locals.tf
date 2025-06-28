locals {
  alb_name            = "eatda-alb"
  deletion_protection = false
  loadbalancer_type   = "application"
  internal            = false

  alb_tags = {
    Environment = local.alb_name
  }
}

locals {
  target_groups = {
    "api-dev" = {
      env                  = "dev"
      port                 = 8080
      protocol             = "HTTP"
      target_type          = "instance"
      deregistration_delay = 60
      health_check = {
        path                = "/actuator/health/"
        protocol            = "HTTP"
        interval            = 30
        matcher             = "200"
        healthy_threshold   = 5
        unhealthy_threshold = 5
      }
    }
    "api-prod" = {
      env                  = "prod"
      port                 = 8080
      protocol             = "HTTP"
      target_type          = "instance"
      deregistration_delay = 60
      health_check = {
        path                = "/actuator/health"
        protocol            = "HTTP"
        interval            = 30
        matcher             = "200"
        healthy_threshold   = 5
        unhealthy_threshold = 5
      }
    }
  }
}


locals {
  http_listener = {
    port     = 80
    protocol = "HTTP"
    type     = "redirect"
    redirect = {
      port        = 443
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

locals {
  https_listener = {
    port                     = 443
    protocol                 = "HTTPS"
    type                     = "forward"
    certificate_arn          = var.certificate_arn
    ssl_policy               = "ELBSecurityPolicy-TLS-1-2-2017-01"
    target_group_arn         = module.target_groups.target_group_arns["api-dev"]
    default_target_group_arn = module.target_groups.target_group_arns["api-prod"]
  }
}

locals {
  listener_rules = {
    "prod-path-rule" = {
      priority         = 1
      host_header      = "api.eatda.net"
      action_type      = "forward"
      target_group_arn = module.target_groups.target_group_arns["api-prod"]
    }
    "dev-path-rule" = {
      priority         = 2
      host_header      = "api-dev.eatda.net"
      action_type      = "forward"
      target_group_arn = module.target_groups.target_group_arns["api-dev"]
    }
  }
}
