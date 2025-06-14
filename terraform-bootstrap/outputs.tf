output "ecr_repo_names" {
  value = {
    for k, v in module.ecr : k => v.repository_url
  }
} 