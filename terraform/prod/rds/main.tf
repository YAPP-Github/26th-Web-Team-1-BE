resource "aws_subnet" "rds_private" {
  count             = 2
  vpc_id            = var.vpc_id
  cidr_block        = var.rds_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = merge(var.tags, {
    Name = "${var.identifier}-rds-private-${count.index + 1}"
  })
}

resource "aws_db_subnet_group" "private" {
  name       = "${var.identifier}-subnet-group"
  subnet_ids = aws_subnet.rds_private[*].id

  tags = merge(var.tags, {
    Name = "${var.identifier}-subnet-group"
  })
}

resource "aws_db_instance" "prod" {
  identifier              = var.identifier
  engine                  = var.engine
  engine_version          = var.engine_version
  instance_class          = var.instance_class
  allocated_storage       = var.allocated_storage
  username                = var.username
  password                = var.password
  vpc_security_group_ids  = var.vpc_security_group_ids
  db_subnet_group_name    = aws_db_subnet_group.private.name
  multi_az                = var.multi_az
  backup_retention_period = var.backup_retention_period
  storage_encrypted       = var.storage_encrypted
  skip_final_snapshot     = true

  tags = var.tags
}
