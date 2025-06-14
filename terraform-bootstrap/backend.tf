terraform {
  backend "s3" {
    bucket         = "timeeat-tf-state"
    key            = "bootstrap/terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "timeeat-tf-lock"
  }
}
