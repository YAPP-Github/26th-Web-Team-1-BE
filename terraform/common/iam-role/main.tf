resource "aws_iam_role" "iam_role" {
  name               = var.name
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
  tags               = var.tags
}

resource "aws_iam_instance_profile" "instance_profile" {
  name = var.name
  role = aws_iam_role.iam_role.name
}

resource "aws_iam_role_policy_attachment" "role_policy_attachment" {
  for_each = toset(var.policy_arns)
  role       = aws_iam_role.iam_role.name
  policy_arn = each.value
}

resource "aws_iam_policy" "custom" {
  for_each = var.custom_inline_policies

  name        = each.value.name
  description = each.value.description
  policy = jsonencode(each.value.policy_document)
  tags        = var.tags
}

resource "aws_iam_role_policy_attachment" "custom_policy_attachment" {
  for_each = aws_iam_policy.custom

  role       = aws_iam_role.iam_role.name
  policy_arn = each.value.arn
}

data "aws_iam_policy_document" "assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = var.assume_role_services
    }
  }
}
