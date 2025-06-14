terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.54.1"
    }
  }
}

provider "aws" {
  region = "ap-northeast-2"
}

resource "aws_s3_bucket" "bootstrap" {
  bucket        = "timeeat-tf-state"
  force_destroy = false

  tags = {
    Name        = "timeeat-tf-state"
    Environment = "state"
    Owner       = "baegam"
    Project     = "time-eat"
    Service     = "infrastructure"
  }
}

resource "aws_s3_bucket_versioning" "bucket_versioning" {
  bucket = aws_s3_bucket.bootstrap.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "bucket_lifecycle" {
  bucket = aws_s3_bucket.bootstrap.id

  rule {
    id     = "state-file-lifecycle"
    status = "Enabled"

    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }

    noncurrent_version_expiration {
      noncurrent_days = 90
    }
  }
}

resource "aws_dynamodb_table" "lock" {
  name         = "timeeat-tf-lock"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    Name        = "timeeat-tf-lock"
    Environment = "state"
    Owner       = "baegam"
    Project     = "time-eat"
    Service     = "infrastructure"
  }
}

module "ecr" {
  source = "../terraform/common/ecr"

  for_each = var.ecr_repositories

  repository_name      = each.key
  scan_on_push         = each.value.scan_on_push
  image_tag_mutability = each.value.image_tag_mutability
  tags                 = each.value.tags
}