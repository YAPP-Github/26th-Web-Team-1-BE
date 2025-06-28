module "vpc" {
  source       = "./vpc"
  project_name = local.project_name
}

module "iam" {
  source      = "./iam"
  group_name  = local.group_name
  policy_arns = local.policy_arns
  user_names  = local.user
}

module "iam_role" {
  source   = "./iam-role"
  for_each = local.iam_roles

  name                 = each.key
  assume_role_services = each.value.assume_role_services
  policy_arns          = each.value.policy_arns
  custom_inline_policies = try(each.value.custom_inline_policies, {})
}

module "security_group" {
  source = "./security-group"

  vpc_id                = module.vpc.vpc_id
  security_groups       = local.security_groups
  ingress_rules         = local.ingress_rules
  egress_rules          = local.egress_rules
  cross_reference_rules = local.cross_reference_rules
  tags                  = local.common_tags
}

module "route53" {
  source            = "./route53"
  domain_name       = local.domain_name
  validation_method = local.validation_method
  record_type       = local.record_type
  subdomains        = local.subdomains
}

module "alb" {
  source = "./alb"
  vpc_id = module.vpc.vpc_id
  subnets = flatten([
    module.vpc.public_subnet_ids.dev,
    module.vpc.public_subnet_ids.prod
  ])
  alb_security_group_id = [module.security_group.security_group_ids["alb"]]
  certificate_arn                 = module.route53.certificate_arn
  certificate_validation_complete = module.route53.certificate_validation_complete
}
