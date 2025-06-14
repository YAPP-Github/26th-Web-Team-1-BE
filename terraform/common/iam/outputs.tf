output "user_name" {
  value = [for user in aws_iam_user.user : user.name]
}
