output "instance_id" {
  value = aws_instance.dev.id
}

output "public_ip" {
  value = aws_eip.dev.public_ip
}

output "private_ip" {
  value = aws_instance.dev.private_ip
}
