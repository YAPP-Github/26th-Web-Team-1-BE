ecs_services = {
  api-dev = {
    task_definition = "api-dev"
    load_balancer = {
      target_group_key = "api-dev"
      container_name   = "api-dev"
      container_port   = 8080
    }
  }

  mysql-dev = {
    task_definition = "mysql"
  }
}

ecs_task_definitions_base = {
  api-dev = {
    cpu          = 256
    memory       = 256
    network_mode = "bridge"
    container_port = [8080]
    host_port = [0]
    requires_compatibilities = ["EC2"]
    environment = {}
    volumes = [
      {
        name      = "dev-api-volume"
        host_path = "/home/ec2-user/eatda/dev/"
      }
    ]
  }

  mysql-dev = {
    cpu               = 256
    memoryReservation = 128
    memory            = 512
    network_mode      = "bridge"
    container_port = [3306]
    host_port = [3306]
    requires_compatibilities = ["EC2"]
    container_image   = "mysql:8"
    environment = {
      MYSQL_DATABASE = "eatda"
    }
    secrets = [
      {
        name      = "MYSQL_USER"
        valueFrom = "/dev/MYSQL_USER_NAME"
      },
      {
        name      = "MYSQL_ROOT_PASSWORD"
        valueFrom = "/dev/MYSQL_ROOT_PASSWORD"
      },
      {
        name      = "MYSQL_PASSWORD"
        valueFrom = "/dev/MYSQL_PASSWORD"
      }
    ]
    volumes = [
      {
        name      = "dev-mysql-volume"
        host_path = "/home/ec2-user/eatda/mysql/"
      }
    ]
  }
}
