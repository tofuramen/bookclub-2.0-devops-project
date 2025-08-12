# Outputs for BookClub infrastructure

output "cluster_id" {
  description = "EKS cluster ID"
  value       = module.eks.cluster_id
}

output "cluster_arn" {
  description = "EKS cluster ARN"
  value       = module.eks.cluster_arn
}

output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "Endpoint for EKS control plane"
  value       = module.eks.cluster_endpoint
}

output "cluster_security_group_id" {
  description = "Security group ids attached to the cluster control plane"
  value       = module.eks.cluster_security_group_id
}

output "cluster_certificate_authority_data" {
  description = "Base64 encoded certificate data required to communicate with the cluster"
  value       = module.eks.cluster_certificate_authority_data
}

output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = aws_ecr_repository.bookclub.repository_url
}

output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = aws_db_instance.bookclub.endpoint
  sensitive   = true
}

output "rds_port" {
  description = "RDS instance port"
  value       = aws_db_instance.bookclub.port
}

output "db_secret_arn" {
  description = "ARN of the database password secret"
  value       = aws_secretsmanager_secret.db_password.arn
}

output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "private_subnets" {
  description = "List of IDs of private subnets"
  value       = module.vpc.private_subnets
}

output "public_subnets" {
  description = "List of IDs of public subnets"
  value       = module.vpc.public_subnets
}

# Information for GitHub Actions
output "github_actions_info" {
  description = "Information needed for GitHub Actions"
  value = {
    aws_region        = var.aws_region
    ecr_repository    = var.ecr_repository_name
    cluster_name      = var.cluster_name
    rds_endpoint      = aws_db_instance.bookclub.endpoint
    db_secret_arn     = aws_secretsmanager_secret.db_password.arn
  }
}

# Instructions for next steps
output "next_steps" {
  description = "Next steps after infrastructure creation"
  value = <<EOF

🎉 BookClub Infrastructure Created Successfully!

📋 GitHub Secrets to Update:
   RDS_ENDPOINT: ${aws_db_instance.bookclub.endpoint}
   ECR_REPOSITORY: ${var.ecr_repository_name}
   AWS_REGION: ${var.aws_region}

🔧 Connect to your cluster:
   aws eks update-kubeconfig --region ${var.aws_region} --name ${var.cluster_name}

🐳 Push your first image:
   aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${aws_ecr_repository.bookclub.repository_url}
   docker tag bookclub-app:latest ${aws_ecr_repository.bookclub.repository_url}:latest
   docker push ${aws_ecr_repository.bookclub.repository_url}:latest

🚀 Ready for CI/CD Pipeline!
   Push to main branch to trigger deployment

EOF
}
