variable "name" {
  description = "IAM Role name"
  type        = string
}

variable "assume_role_services" {
  description = "List of services that can assume this role"
  type = list(string)
}

variable "policy_arns" {
  description = "List of policy ARNs to attach to the role"
  type = list(string)
  default = []
}

variable "tags" {
  description = "Tags to apply to the role"
  type = map(string)
  default = {}
}
