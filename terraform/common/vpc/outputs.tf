output "vpc_id" {
  description = "The ID of the VPC"
  value       = aws_vpc.vpc.id
}

output "vpc_cidr_block" {
  description = "The CIDR block of the VPC"
  value       = aws_vpc.vpc.cidr_block
}

output "availability_zones" {
  description = "List of availability zones"
  value       = var.availability_zones
}

output "public_subnet_ids" {
  description = "Map of public subnet IDs for environments"
  value = {
    dev  = aws_subnet.public[0].id
    prod = aws_subnet.public[1].id
  }
}

output "private_subnet_ids" {
  description = "List of IDs of private subnets"
  value = {
    dev  = aws_subnet.private[0].id
    prod = aws_subnet.private[1].id
  }
}

output "internet_gateway_id" {
  description = "ID of the Internet Gateway"
  value       = aws_internet_gateway.igw.id
}

output "public_route_table_id" {
  description = "ID of the public route table"
  value       = aws_route_table.public_rt.id
}

output "private_route_table_ids" {
  description = "List of IDs of private route tables"
  value       = aws_route_table.private_rt[*].id
}