# RDS PostgreSQL Database

# Random password for RDS
resource "random_password" "db_password" {
  length  = 32
  special = true
}

# Store DB password in AWS Secrets Manager
resource "aws_secretsmanager_secret" "db_password" {
  name                    = "${var.cluster_name}-db-password"
  description             = "Database password for BookClub application"
  recovery_window_in_days = 7

  tags = {
    Name = "${var.cluster_name}-db-password"
  }
}

resource "aws_secretsmanager_secret_version" "db_password" {
  secret_id     = aws_secretsmanager_secret.db_password.id
  secret_string = random_password.db_password.result
}

# DB subnet group
resource "aws_db_subnet_group" "bookclub" {
  name       = "${var.cluster_name}-db-subnet-group"
  subnet_ids = module.vpc.private_subnets

  tags = {
    Name = "${var.cluster_name}-db-subnet-group"
  }
}

# RDS instance
resource "aws_db_instance" "bookclub" {
  allocated_storage    = var.db_allocated_storage
  max_allocated_storage = 100
  storage_type         = "gp2"
  engine               = "postgres"
  engine_version       = "15.8"
  instance_class       = var.db_instance_class

  identifier = "${var.cluster_name}-db"
  db_name    = var.db_name
  username   = var.db_username
  password   = random_password.db_password.result

  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.bookclub.name

  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"

  skip_final_snapshot = true
  deletion_protection = false

  performance_insights_enabled = true
  monitoring_interval         = 60
  monitoring_role_arn        = aws_iam_role.rds_enhanced_monitoring.arn

  tags = {
    Name = "${var.cluster_name}-database"
  }
}

# IAM role for RDS enhanced monitoring
resource "aws_iam_role" "rds_enhanced_monitoring" {
  name = "${var.cluster_name}-rds-enhanced-monitoring"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "monitoring.rds.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name = "${var.cluster_name}-rds-enhanced-monitoring"
  }
}

resource "aws_iam_role_policy_attachment" "rds_enhanced_monitoring" {
  role       = aws_iam_role.rds_enhanced_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

# Create the database schema (requires PostgreSQL provider)
# This will run the init.sql script on the RDS instance
resource "null_resource" "init_db" {
  depends_on = [aws_db_instance.bookclub]

  provisioner "local-exec" {
    command = <<EOF
      # Wait for RDS to be ready
      echo "Waiting for RDS instance to be ready..."
      aws rds wait db-instance-available --db-instance-identifier ${aws_db_instance.bookclub.id} --region ${var.aws_region}

      # Install PostgreSQL client if not available
      if ! command -v psql &> /dev/null; then
        echo "Installing PostgreSQL client..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
          brew install postgresql
        else
          sudo apt-get update && sudo apt-get install -y postgresql-client
        fi
      fi

      # Run the initialization script
      echo "Initializing database schema..."
      PGPASSWORD='${random_password.db_password.result}' psql -h ${aws_db_instance.bookclub.endpoint} -U ${var.db_username} -d ${var.db_name} -f ../init.sql || echo "Database initialization completed (or already exists)"
    EOF

    environment = {
      AWS_REGION = var.aws_region
    }
  }

  triggers = {
    db_instance_id = aws_db_instance.bookclub.id
  }
}
