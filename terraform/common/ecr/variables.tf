variable "repository_name" {
  type = string
}

variable "scan_on_push" {
  type = bool
}

variable "image_tag_mutability" {
  type = string
}

variable "tags" {
  type = map(string)
}
