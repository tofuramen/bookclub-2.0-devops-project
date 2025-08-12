# EKS Access Configuration
# This fixes the kubectl authentication issue

# Add current user as cluster admin with proper policy
resource "aws_eks_access_entry" "admin_user" {
  cluster_name  = module.eks.cluster_name
  principal_arn = data.aws_caller_identity.current.arn
  type         = "STANDARD"
}

# Associate admin policy with the access entry
resource "aws_eks_access_policy_association" "admin_policy" {
  cluster_name  = module.eks.cluster_name
  principal_arn = data.aws_caller_identity.current.arn
  policy_arn    = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy"

  access_scope {
    type = "cluster"
  }

  depends_on = [aws_eks_access_entry.admin_user]
}

# Output the user that was added
output "eks_admin_user" {
  description = "AWS user added as EKS admin"
  value       = data.aws_caller_identity.current.arn
}
