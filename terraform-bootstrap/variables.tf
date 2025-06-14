variable "ecr_repositories" {
  type = map(object({
    scan_on_push         = bool
    image_tag_mutability = string
    tags = map(string)
  }))
}
