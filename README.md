# Photo Archival

Photo Archival is a Java-based project designed to help organize, manage, and archive your photo collections
efficiently. Built with Java 21 and MongoDB, this application receives updates
from [Photo Archival Lambda](https://github.com/thalesmaiaa/photo-archival-lambda)
that is triggered by S3 events after updating an image into a S3 bucket. This image is sent to the Rekognition service
for face detection and label detection, and the results are send back to this application to process the information and
makes it available for each user photo. The application provides powerful tools for categorizing, tagging, and filtering
photos based on various attributes.

## Features

- Organize photos into folders.
- Search and retrieve photos efficiently.
- Integration with AWS S3 for storage.
- Receives information from Rekognition after face detection and label detection.

## AWS Integration

- The project uses an AWS Lambda function to process updates triggered by S3 events.
  Ensure your AWS credentials and S3 bucket configurations are properly set up.
- Mongo DB is used for storing photo metadata and attributes.
  Make sure to configure the MongoDB connection settings in the `application.yml` file.
- The application uses AWS SDK for Java to interact with S3 and Lambda services.
  Ensure you have the necessary permissions set up in your AWS IAM roles.

## Functionalities

The Photo Archival application includes several workflows to ensure seamless photo management and integration with AWS
services:

### 1. Photo Upload and Processing

- **Trigger**: A photo is uploaded to an AWS S3 bucket.
- **Process**:
    - An S3 event triggers the [Photo Archival Lambda](https://github.com/thalesmaiaa/photo-archival-lambda).
    - The Lambda function sends the photo to AWS Rekognition for face and label detection.
    - The detection results are sent back to the Photo Archival application via an API call.
- **Outcome**: The application processes the metadata and updates the photo's attributes in MongoDB.

### 2. Metadata Storage

- **Trigger**: Metadata is received from the Lambda function.
- **Process**:
    - The application stores the metadata in MongoDB.
    - Metadata includes detected faces, labels, and other attributes.
- **Outcome**: Users can search, filter, and categorize photos based on the stored metadata.

### 3. Photo Retrieval

- **Trigger**: A user searches for photos using specific criteria.
- **Process**:
    - The application queries MongoDB for matching metadata.
    - Matching photos are retrieved from the S3 bucket.
- **Outcome**: Users can view and manage their photos efficiently.

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

## Installation and Usage

1. Clone the repository:
   ```bash
   git clone https://github.com/thalesmaiaa/photoarchival.git
   ```
2. Navigate to the project directory:
   ```bash
   cd photoarchival
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

4. Start the application:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

Don't forget to set up your MongoDB connection in the `application.yml` file.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature-name"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Open a pull request.
