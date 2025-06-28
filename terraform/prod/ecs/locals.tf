locals {
  cluster_name        = "${var.environment}-cluster"
  launch_type         = "EC2"
  scheduling_strategy = "DAEMON"

  settings = {
    name  = "containerInsights"
    value = "disabled"
  }

  deployment_controller_type = "ECS"

  resolved_task_definitions = {
    for name, def in var.ecs_task_definitions :
    name => name == "api-prod" ? merge(def, {
      task_definition_name = "api-prod"
      container_image      = "${var.ecr_repo_urls["prod"]}:placeholder"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {}
      secrets = []
    }) : name == "datadog" ? merge(def, {
      task_definition_name = "datadog-agent"
      container_image      = "public.ecr.aws/datadog/agent:latest"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {
        DD_SITE                              = "us5.datadoghq.com"
        DD_PROCESS_AGENT_ENABLED             = "true"
        DD_APM_ENABLED                       = "true"
        DD_LOGS_ENABLED                      = "true"
        DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL = "true"
        DD_APM_RECEIVER_PORT                 = "8126"
        DD_APM_NON_LOCAL_TRAFFIC             = "true"
      }
      secrets = [
        {
          name      = "DD_API_KEY"
          valueFrom = "/prod/DD_API_KEY"
        }
      ]
    }) : merge(def, {
      task_definition_name = "dummy"
      container_image      = "dummy"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {}
      secrets = [
        {
          name      = "DUMMY_${name}"
          valueFrom = "/dummy"
        }
      ]
    })
  }

  resolved_ecs_services = {
    for name, def in var.ecs_services : name => {
      name         = name
      iam_role_arn = var.ecs_task_definitions[name].task_role_arn
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

        command = svc == "api-prod" ? [
          "java",
          "-Dspring.profiles.active=prod",
          "-jar",
          "/api.jar"
        ] : null

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
            sourceVolume  = vol.name
            containerPath = vol.host_path
            readOnly      = false
          }
        ]
      }
    ]
  }
}
