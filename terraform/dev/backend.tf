terraform {
  backend "s3" {
    bucket         = "timeeat-tf-state"
    key            = "dev/terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "timeeat-tf-lock"
  }
}
