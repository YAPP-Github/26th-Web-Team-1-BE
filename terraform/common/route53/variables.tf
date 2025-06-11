variable "domain_name" {
  description = "time-eat.com"
  type        = string
}

variable "subdomains" {
  description = "map of subdomains and their ALB info"
  type = map(object({
    alb_dns_name = string
    alb_zone_id  = string
  }))
}

variable "validation_method" {
  type = string
}

variable "recode_type" {
  type = string
}
