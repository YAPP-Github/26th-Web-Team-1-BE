variable "subnets" {
  type = list(string)
  description = "List of public subnet IDs for the ALB"
}

variable "alb_security_group_id" {
  type = list(string)
  description = "Security group ID for the ALB"
}

variable "vpc_id" {}

variable "certificate_arn" {
  type = string
}

variable "certificate_validation_complete" {
  type = string
}