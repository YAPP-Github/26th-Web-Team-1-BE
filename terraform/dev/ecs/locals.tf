locals {
  cluster_name        = "${var.environment}-cluster"
  launch_type         = "EC2"
  scheduling_strategy = "REPLICA"

  settings = {
    name  = "containerInsights"
    value = "disabled"
  }

  deployment_controller_type = "ECS"

  resolved_task_definitions = {
    for name, def in var.ecs_task_definitions :
    name => name == "api-dev" ? merge(def, {
      task_definition_name = "api-dev"
      container_image      = "${var.ecr_repo_names["dev"]}:latest"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {}
      secrets = []
    }) : name == "mysql-dev" ? merge(def, {
      task_definition_name = "mysql-dev"
      container_image      = "mysql:8"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {
        MYSQL_DATABASE = "time-eat"
      }
      secrets = [
        {
          name      = "MYSQL_USER"
          valueFrom = "/dev/mysql-name"
        },
        {
          name      = "MYSQL_ROOT_PASSWORD"
          valueFrom = "/dev/mysql-root-pw"
        },
        {
          name      = "MYSQL_PASSWORD"
          valueFrom = "/dev/mysql-pw"
        }
      ]
    }) : merge(def, {
      task_definition_name = "dummy"
      container_image      = "dummy"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {
        MYSQL_DATABASE = "dummy"
      }
      secrets = [
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        },
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        },
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        }
      ]
    })
  }

  resolved_ecs_services = {
    for name, def in var.ecs_services : name => {
      name          = name
      desired_count = def.desired_count
      iam_role_arn  = var.ecs_task_definitions[name].task_role_arn
      load_balancer = try(def.load_balancer, null)
    }
  }

  container_definitions_map = {
    for svc, def in local.resolved_task_definitions : svc => [
      {
        name      = def.task_definition_name
        image     = def.container_image
        cpu       = def.cpu
        memory    = def.memory
        essential = true
        stopTimeout = lookup(def, "stop_timeout", var.default_stop_timeout)

        portMappings = [
          for idx, port in def.container_port : {
            name          = "${svc}-${port}-tcp"
            containerPort = port
            hostPort      = def.host_port[idx]
            protocol = lookup(def, "protocol", var.default_protocol)
          }
        ]

        environment = [
          for k, v in def.environment : {
            name  = k
            value = v
          }
        ]

        secrets = [
          for s in def.secrets : {
            name      = s.name
            valueFrom = s.valueFrom
          }
        ]

        mountPoints = [
          for vol in (def.volumes != null ? def.volumes : []) : {
            sourceVolume = vol.name
            containerPath = lookup(var.volume_mount_paths, vol.name, "/time-eat")
            readOnly     = false
          }
        ]
      }
    ]
  }
}
