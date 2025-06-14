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

data "aws_iam_policy_document" "assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = var.assume_role_services
    }
  }
}
