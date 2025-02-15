variable "image_name_dev" {
  type = string
}
module "dev" {
  source         = "../../infra"
  iamRole        = "dev"
  environment    = "dev"
  container_name = "homologacao"
  image_name = var.image_name_dev
}

output "dns_alb" {
  value = module.dev.dns
}
