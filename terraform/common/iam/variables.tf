variable "group_name" {
  description = "IAM group name"
  type        = string
}

variable "user_names" {
  description = "IAM user name"
  type = list(string)
}

variable "policy_arns" {
  description = "List of IAM Policy ARNs to attach to the group"
  type = list(string)
}

variable "enable_mfa_enforcement" {
  description = "Whether to enforce MFA via IAM policy"
  type        = bool
  default     = true
}

variable "tags" {
  description = "Tags for the IAM user"
  type = map(string)
  default = {}
}
