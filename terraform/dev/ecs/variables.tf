variable "environment" {
  type = string
}

variable "ecr_repo_names" {
  type = map(string)
}

variable "ecs_task_definitions" {
  type = map(object({
    cpu                = number
    memory             = number
    network_mode       = string
    container_port = list(number)
    host_port = list(number)
    environment = map(string)
    container_image    = string
    execution_role_arn = string
    task_role_arn      = string
    requires_compatibilities = list(string)
    volumes = list(object({
      name      = string
      host_path = string
    }))
  }))
}

variable "ecs_services" {
  type = map(object({
    desired_count = number
    load_balancer = optional(object({
      target_group_key = string
      container_name   = string
      container_port   = number
    }))
  }))
}

variable "default_stop_timeout" {
  type    = number
  default = 30
}

variable "default_protocol" {
  type    = string
  default = "tcp"
}

variable "volume_mount_paths" {
  type = map(string)
  default = {}
}

variable "alb_target_group_arns" {
  type = map(string)
}

variable "tags" {
  type = map(string)
  default = {}
}
