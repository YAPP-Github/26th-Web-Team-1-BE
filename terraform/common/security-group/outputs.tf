output "security_group_ids" {
  value = {
    for k, v in aws_security_group.common : k => v.id
  }
}

output "security_group_names" {
  value = {
    for k, v in aws_security_group.common : k => v.name
  }
}
