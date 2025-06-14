variable "ecs_task_definitions" {
  description = "Resolved task definition map (with image, roles, etc)"
  type = map(object({
    cpu                = number
    memory             = number
    network_mode       = string
    container_image    = string
    task_role_arn      = string
    execution_role_arn = string
    requires_compatibilities = list(string)
    container_port = list(number)
    host_port = list(number)
    volumes = optional(list(object({
      name      = string
      host_path = string
    })), [])
    environment = optional(map(string), {})
  }))
}

variable "container_definitions_map" {
  description = "Map of ECS service names to their container definitions"
  type = map(any)
}

variable "tags" {
  type = map(string)
  default = {}
}
