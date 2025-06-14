output "certificate_arn" {
  value = aws_acm_certificate.wildcard.arn
}

output "certificate_validation_complete" {
  value = aws_acm_certificate_validation.wildcard.id
}
