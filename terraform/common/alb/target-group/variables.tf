variable "target_groups" {
  type = map(object({
    port        = number
    protocol    = string
    target_type = string
    health_check = object({
      path                = string
      protocol            = string
      interval            = number
      matcher             = string
      healthy_threshold   = number
      unhealthy_threshold = number
    })
  }))
}


variable "vpc_id" {
  type        = string
}