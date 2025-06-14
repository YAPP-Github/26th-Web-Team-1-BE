variable "alb_arn" {}

variable "http_listener" {
  type = object({
    port     = number
    protocol = string
    type     = string
    redirect = object({
      port        = number
      protocol    = string
      status_code = string
    })
  })
}
