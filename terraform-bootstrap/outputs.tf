output "ecr_repo_urls" {
  value = {
    for k, v in module.ecr : k => v.repository_url
  }
}

output "ecr_repo_names" {
  value = {
    for k, v in module.ecr : k => v.repository_name
  }
}
