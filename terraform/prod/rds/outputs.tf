output "endpoint" {
  description = "RDS endpoint address"
  value       = aws_db_instance.prod.endpoint
}

output "arn" {
  description = "RDS instance ARN"
  value       = aws_db_instance.prod.arn
}