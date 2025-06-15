# CI/CD Workflows:

## Build and Push Java Docker Image to AWS ECR

This GitHub Actions workflow automates the process of building a Java application, creating a Docker image,
and pushing it to Amazon Elastic Container Registry (ECR) whenever code is pushed to the `develop` branch.

### Workflow Overview

- **Trigger:** Runs on every push to the `develop` branch.
- **Key Steps:**
  1. **Checkout source code** from the repository.
  2. **Set up JDK 21** using Amazon Corretto distribution with Maven cache enabled for faster builds.
  3. **Build the Java project** using Maven (`mvn clean install`).
  4. **Configure AWS credentials** by assuming a specified IAM role via OpenID Connect (OIDC) with permissions stored
     as GitHub secrets.
  5. **Authenticate Docker with Amazon ECR** using the official AWS login action.
  6. **Build the Docker image** tagged as `photo-archival`.
  7. **Tag the Docker image** with the full ECR repository URI.
  8. **Push the Docker image** to the configured Amazon ECR repository.

## Deploy to EC2 via Instance Connect

This GitHub Actions workflow automates deploying your application by remotely executing a deployment script
on an EC2 instance using AWS EC2 Instance Connect.

### Workflow Overview

- **Trigger:** Runs on every push to the `main` branch.
- **Key Steps:**
  1. **Configure AWS CLI** with permissions to assume a role via OIDC, using secrets for credentials.
  2. **Generate a temporary SSH key pair** dynamically during the workflow.
  3. **Push the SSH public key** to the EC2 instance using AWS EC2 Instance Connect API, allowing secure, temporary
     SSH access.
  4. **SSH into the EC2 instance** using the temporary private key and execute the deployment script (`deploy.sh`) as
     root via `sudo`.

## Required Secrets

- `AWS_ASSUME_ROLE_ARN`: ARN of the IAM role to assume for AWS API access.
- `AWS_REGION`: AWS region where your EC2 instance is located.
- `AWS_ACCOUNT_ID`: Your AWS account ID.
- `ECR_REPOSITORY`: The name of your Amazon ECR repository.
- `EC2_INSTANCE_ID`: The ID of your EC2 instance.
- `EC2_AVAILABILITY_ZONE`: Availability zone of your EC2 instance.
- `EC2_USER`: Username on the EC2 instance (commonly `ec2-user`).
- `EC2_PUBLIC_DNS`: Public DNS name or IP address of your EC2 instance.

## Notes

- The deployment script `/home/ec2-user/deploy.sh` must exist on the EC2 instance and be executable.
- Using Instance Connect avoids storing permanent SSH keys in GitHub Secrets, improving security.
- The workflow runs the deployment script with root privileges (`sudo`) to perform privileged operations if needed.
