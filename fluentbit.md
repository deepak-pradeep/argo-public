### Options for Deploying Fluent Bit on EKS:

- Use the official Fluent Bit Helm chart
- Use the Amazon CloudWatch Observability Add-on
- Use the Amazon-managed Helm chart via the AWS-IA Terraform module

### Why We Chose Option 3 (Amazon-Managed Helm via AWS-IA Module):

We decided to go with the Amazon-managed Helm chart deployed through the AWS-IA module.

#### ✅ Reasons:

- Lightweight – No unwanted additional pods. Unlike the generic Fluent Bit Helm chart, this is tailored specifically for AWS.

- No extra add-ons needed – it comes focused and pre-configured for AWS environments.

- Terraform module support – Makes the implementation and upgrade process much simpler and more consistent.

- Flexible – Customizations are easy and well-supported.

### Deployment Process:
To deploy using this method, you just need to pass the following values:

```
namespace            = "<your-namespace>"
create_namespace     = true
aws_for_fluentbit    = {}

```

***Note: Roles, namespace, permissions, and cloud configurations can be fully customized.***
