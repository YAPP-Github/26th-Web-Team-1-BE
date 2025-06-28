terraform {
  backend "s3" {
    bucket         = "eatda-tf-state"
    key            = "common/terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "eatda-tf-lock"
  }
}
