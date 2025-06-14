variable "vpc_id" {
  type = string
}

variable "tags" {
  type = map(string)
  default = {}
}

variable "security_groups" {
  type = map(object({
    name        = string
    description = string
    tags = map(string)
  }))
}

variable "ingress_rules" {
  type = map(object({
    security_group_key = string
    from_port          = number
    to_port            = number
    protocol           = string
    cidr_blocks = list(string)
    description        = string
  }))
}

variable "egress_rules" {
  type = map(object({
    security_group_key = string
    from_port          = number
    to_port            = number
    protocol           = string
    cidr_blocks = list(string)
    description        = string
  }))
}

variable "cross_reference_rules" {
  type = map(object({
    source_security_group_key = string
    target_security_group_key = string
    from_port                 = number
    to_port                   = number
    protocol                  = string
    description               = string
  }))
}
