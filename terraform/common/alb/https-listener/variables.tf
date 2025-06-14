variable "alb_arn" {}

variable "https_listener" {
  type = object({
    port                     = number
    protocol                 = string
    type                     = string
    certificate_arn          = string
    target_group_arn         = string
    ssl_policy               = string
    default_target_group_arn = string
  })
}

variable "listener_rules" {
  type = map(object({
    priority         = number
    host_header      = string
    action_type      = string
    target_group_arn = string
  }))
}

