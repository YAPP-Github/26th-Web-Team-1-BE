data "aws_route53_zone" "common" {
  name         = var.domain_name
  private_zone = false
}

resource "aws_acm_certificate" "wildcard" {
  domain_name       = "*.${var.domain_name}"
  validation_method = var.validation_method

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "cert_validation" {
  for_each = {
    for dvo in aws_acm_certificate.wildcard.domain_validation_options :
    dvo.domain_name => {
      name  = dvo.resource_record_name
      type  = dvo.resource_record_type
      value = dvo.resource_record_value
    }
  }

  zone_id = data.aws_route53_zone.common.zone_id  # 변경된 부분
  name = each.value.name
  type = each.value.type
  ttl  = 60
  records = [each.value.value]
}

resource "aws_acm_certificate_validation" "wildcard" {
  certificate_arn         = aws_acm_certificate.wildcard.arn
  validation_record_fqdns = [for record in aws_route53_record.cert_validation : record.fqdn]
  timeouts {
    create = "10m"
  }
}

resource "aws_route53_record" "subdomains" {
  for_each = var.subdomains

  zone_id = data.aws_route53_zone.common.zone_id
  name    = "${each.key}.${var.domain_name}"
  type    = var.record_type

  alias {
    name                   = each.value.alb_dns_name
    zone_id                = each.value.alb_zone_id
    evaluate_target_health = true
  }
}
