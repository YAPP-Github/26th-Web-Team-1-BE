output "alb_arn" {
  value = aws_alb.common.arn
}

output "target_group_arns" {
  value = module.target_groups.target_group_arns
}

output "target_group_names" {
  value = module.target_groups.target_group_names
}

output "alb_dns_name" {
  value = aws_alb.common.dns_name
}

output "alb_zone_id" {
  value = aws_alb.common.zone_id
}