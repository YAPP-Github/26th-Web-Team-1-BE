variable "ecs_services" {
  type = map(object({
    desired_count = number
    load_balancer = object({
      target_group_key = string
      container_name   = string
      container_port   = number
    })
  }))
}

variable "cluster_id" {
  type = string
}

variable "deployment_controller_type" {
  type = string
}

variable "launch_type" {
  type = string
}

variable "scheduling_strategy" {
  type = string
}

variable "task_definition_arn" {
  type = map(string)
}

variable "alb_target_group_arns" {
  type = map(string)
}

variable "tags" {
  type = map(string)
  default = {}
}