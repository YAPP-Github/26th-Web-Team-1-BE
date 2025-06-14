output "target_group_arns" {
  value = {
    for k, tg in aws_lb_target_group.common :
    k => tg.arn
  }
}

output "target_group_names" {
  value = {
    for k, tg in aws_lb_target_group.common :
    k => tg.name
  }
}
