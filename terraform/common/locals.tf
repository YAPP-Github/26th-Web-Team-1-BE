locals {
  group_name   = "power"
  project_name = "time-eat"

  policy_arns = [
    "arn:aws:iam::aws:policy/AdministratorAccess",
    "arn:aws:iam::aws:policy/AmazonElasticContainerRegistryPublicPowerUser",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    "arn:aws:iam::aws:policy/ElasticLoadBalancingReadOnly"
  ]

  user = ["roy-test", "leegwichan"]

  common_tags = {
    Project   = local.project_name
    ManagedBy = "terraform"
  }
}

locals {
  iam_roles = {
    "ec2-to-ecs" = {
      assume_role_services = ["ec2.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role",
        "arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess",
        "arn:aws:iam::aws:policy/AmazonECS_FullAccess",
        "arn:aws:iam::aws:policy/AmazonS3FullAccess",
        "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
      ]
      tags = {
        Purpose = "ECS EC2 Registration"
      }
    }

    "ecsTaskExecutionRole" = {
      assume_role_services = ["ecs-tasks.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
        "arn:aws:iam::aws:policy/AmazonS3FullAccess",
        "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly",
        "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess",
        "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess"
      ]
      tags = {
        Purpose = "ECS Task Execution"
      }
    }

    "ecsAppTaskRole" = {
      assume_role_services = ["ecs-tasks.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/AmazonS3FullAccess",
        "arn:aws:iam::aws:policy/SecretsManagerReadWrite"
      ]
      tags = {
        Purpose = "ECS Application Task Role"
      }
    }
  }
}

locals {
  domain_name       = "time-eat.com"
  validation_method = "DNS"
  record_type       = "A"
  alb_alias = {
    alb_dns_name = module.alb.alb_dns_name
    alb_zone_id  = module.alb.alb_zone_id
  }

  subdomains = {
    api = local.alb_alias
    dev = local.alb_alias
  }
}

locals {
  security_groups = {
    alb = {
      name        = "timeeat-alb-sg"
      description = "ALB SG"
      tags = {
        Name        = "timeeat-alb-sg"
        Environment = "common"
        Service     = "alb"
      }
    }
    ec2 = {
      name        = "timeeat-ec2-sg"
      description = "EC2 SG"
      tags = {
        Name        = "timeeat-ec2-sg"
        Environment = "common"
        Service     = "ec2"
      }
    }
    rds = {
      name        = "timeeat-rds-sg"
      description = "RDS SG"
      tags = {
        Name        = "timeeat-rds-sg"
        Environment = "common"
        Service     = "rds"
      }
    }
  }

  ingress_rules = {
    alb_http = {
      security_group_key = "alb"
      from_port          = 80
      to_port            = 80
      protocol           = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description        = "HTTP"
    }
    alb_https = {
      security_group_key = "alb"
      from_port          = 443
      to_port            = 443
      protocol           = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description        = "HTTPS"
    }
    ec2_ssh = {
      security_group_key = "ec2"
      from_port          = 22
      to_port            = 22
      protocol           = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description        = "SSH"
    }
  }

  egress_rules = {
    alb_egress = {
      security_group_key = "alb"
      from_port          = 0
      to_port            = 0
      protocol           = "-1"
      cidr_blocks = ["0.0.0.0/0"]
      description        = "Allow all"
    }
    ec2_egress = {
      security_group_key = "ec2"
      from_port          = 0
      to_port            = 0
      protocol           = "-1"
      cidr_blocks = ["0.0.0.0/0"]
      description        = "Allow all"
    }
    rds_egress = {
      security_group_key = "rds"
      from_port          = 0
      to_port            = 0
      protocol           = "-1"
      cidr_blocks = ["0.0.0.0/0"]
      description        = "Allow all"
    }
  }

  cross_reference_rules = {
    ec2_from_alb_http = {
      source_security_group_key = "alb"
      target_security_group_key = "ec2"
      from_port                 = 8080
      to_port                   = 8080
      protocol                  = "tcp"
      description               = "HTTP from ALB"
    }
    ec2_from_alb_dynamic = {
      source_security_group_key = "alb"
      target_security_group_key = "ec2"
      from_port                 = 32768
      to_port                   = 60999
      protocol                  = "tcp"
      description               = "Dynamic ports from ALB"
    }
    rds_from_ec2 = {
      source_security_group_key = "ec2"
      target_security_group_key = "rds"
      from_port                 = 3306
      to_port                   = 3306
      protocol                  = "tcp"
      description               = "MySQL from EC2"
    }
  }
}




