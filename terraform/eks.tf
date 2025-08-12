# EKS Cluster Configuration

module "eks" {
  source = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"  # Updated to latest stable version

  cluster_name    = var.cluster_name
  cluster_version = var.cluster_version

  vpc_id                         = module.vpc.vpc_id
  subnet_ids                     = module.vpc.private_subnets
  cluster_endpoint_public_access = true

  # EKS Managed Node Groups
  eks_managed_node_groups = {
    workers = {  # Shortened from "main"
      name = "workers"  # Shortened name

      instance_types = var.instance_types
      capacity_type  = "ON_DEMAND"

      min_size     = var.min_size
      max_size     = var.max_size
      desired_size = var.desired_size

      disk_size = 50

      # Kubernetes labels
      labels = {
        Environment = var.environment
        NodeGroup   = "workers"
      }

      tags = {
        ExtraTag = "EKS-managed-node-group"
      }
    }
  }

  tags = {
    Environment = var.environment
    Terraform   = "true"
  }
}
