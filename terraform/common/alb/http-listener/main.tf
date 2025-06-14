resource "aws_alb_listener" "http" {
  load_balancer_arn = var.alb_arn
  port              = var.http_listener.port
  protocol          = var.http_listener.protocol

  default_action {
    type = var.http_listener.type

    redirect {
      port        = var.http_listener.redirect.port
      protocol    = var.http_listener.redirect.protocol
      status_code = var.http_listener.redirect.status_code
    }
  }
}