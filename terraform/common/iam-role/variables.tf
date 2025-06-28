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

variable "custom_inline_policies" {
  description = "A map of custom IAM policies to create and attach. The key is a unique name for the policy."
  type = map(object({
    name            = string
    description = optional(string)
    policy_document = any
  }))
  default = {}
}

variable "tags" {
  description = "Tags to apply to the role"
  type = map(string)
  default = {}
}
