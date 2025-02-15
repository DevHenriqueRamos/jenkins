terraform {
  backend "s3" {
    bucket = "java-api-terraform-state-proghenrique"
    key    = "prod/terraform.tfstate"
    region = "us-west-2"
  }
}
