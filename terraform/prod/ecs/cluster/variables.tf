variable "cluster_name" {
  description = "prod cluster name"
  default     = "prod-cluster"
}

variable "cluster_settings" {
  type = object({
    name  = string
    value = string
  })

  validation {
    condition     = var.cluster_settings.name == "containerInsights"
    error_message = "Only 'containerInsights' is supported for ECS cluster setting.name."
  }
}

variable "tags" {
  type = map(string)
  default = {}
}