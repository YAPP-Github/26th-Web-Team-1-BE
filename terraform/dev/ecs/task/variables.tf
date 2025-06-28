variable "ecs_task_definitions" {
  description = "A strictly-typed, unified map for all task definition properties."

  type = map(object({
    cpu                = number
    memory             = number
    network_mode       = string
    task_role_arn      = string
    execution_role_arn = string
    requires_compatibilities = list(string)
    volumes = optional(list(object({
      name      = string
      host_path = string
    })), [])

    container_definitions = list(object({
      name        = string
      image       = string
      cpu         = number
      memory      = number
      essential   = bool
      stopTimeout = number

      command = optional(list(string))

      portMappings = list(object({
        name          = string
        containerPort = number
        hostPort      = number
        protocol      = string
      }))

      environment = optional(list(object({
        name  = string
        value = string
      })), [])

      secrets = optional(list(object({
        name      = string
        valueFrom = string
      })), [])

      mountPoints = optional(list(object({
        sourceVolume  = string
        containerPath = string
        readOnly      = bool
      })), [])
    }))
  }))
}

variable "tags" {
  type = map(string)
  default = {}
}
