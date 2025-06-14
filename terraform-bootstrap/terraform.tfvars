ecr_repositories = {
  prod = {
    scan_on_push         = false
    image_tag_mutability = "MUTABLE"
    tags = {
      Name    = "time-eat-prod"
      Service = "prod"
    }
  }

  dev = {
    scan_on_push         = false
    image_tag_mutability = "MUTABLE"
    tags = {
      Name    = "time-eat-dev"
      Service = "dev"
    }
  }
}
