variable "ecs_services" {
  type = map(object({
    load_balancer = optional(object({
      target_group_key = string
      container_name   = string
      container_port   = number
    }))
  }))
}

variable "ecs_task_definitions_base" {
  type = map(object({
    cpu          = number
    memory       = number
    network_mode = string
    container_port = list(number)
    host_port = list(number)
    environment = map(string)
    requires_compatibilities = optional(list(string))
    container_image = optional(string)
    execution_role_arn = optional(string)
    task_role_arn = optional(string)
    volumes = list(object({
      name      = string
      host_path = string
    }))
  }))
}

variable "volume_mount_paths" {
  description = "A map of volume names to their mount paths inside the container."
  type = map(string)
  default = {}
}
