variable "instance_definitions" {
  description = "EC2 instance definitions for different roles including AMI and instance type"
  type = object({
    role          = string
    ami           = string
    instance_type = string
    iam_instance_profile = optional(string)
    key_name      = string
    user_data = optional(string)
  })
}

variable "instance_subnet_map" {
  type = map(string)
}

variable "name_prefix" {
  description = "Name prefix for instance naming"
  type        = string
}

variable "ec2_sg_id" {
  description = "Security Group ID for EC2 instances"
  type        = string
}

variable "tags" {
  type = map(string)
  default = {}
}
