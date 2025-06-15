#!/bin/bash

# stop previous containers running on port 5001, 5000
docker stop photo_archival
docker rm photo_archival

# Ensure Docker is running
if ! systemctl is-active --quiet docker; then
    echo "Docker is not running. Starting Docker..."
    sudo systemctl start docker
fi

# Remove previous environment file
rm -rf env.list

# Start MongoDB service (if needed)
sudo systemctl start mongod

# Set AWS region (replace with your region)
export AWS_REGION='us-east-1'

# Fetch secrets from AWS Secrets Manager (replace with your secret id)
SECRET_JSON=$(aws secretsmanager get-secret-value --secret-id <your-secret-id> --query SecretString --output text)

# Get public IP of the instance (replace with your instance id)
PUBLIC_IP=$(aws ec2 describe-instances --instance-ids <your-instance-id> --query 'Reservations[*].Instances[*].PublicIpAddress' --output text)

# Update the secret with the new public IP (example updates MONGODB_HOST)
UPDATED_SECRET=$(echo "$SECRET_JSON" | jq --arg ip "$PUBLIC_IP" '.MONGODB_HOST = $ip')
aws secretsmanager update-secret --secret-id <your-secret-id> --secret-string "$UPDATED_SECRET"

# Update Lambda function environment variable with the new API URL (replace with your function name)
HOST=$(aws secretsmanager get-secret-value --secret-id <your-secret-id> --query 'SecretString' --output text | jq -r '.MONGODB_HOST')
PORT=$(aws secretsmanager get-secret-value --secret-id <your-secret-id> --query 'SecretString' --output text | jq -r '.MONGODB_PORT')
export FULL_URL="http://${HOST}:5000"
aws lambda update-function-configuration --function-name <your-lambda-function> --environment "Variables={API_URL=${FULL_URL}}" --query 'LastModified' --output text

# Extract values from the secret JSON
export AWS_REGION=$(echo "$SECRET_JSON" | jq -r '.AWS_REGION')
export BUCKET_NAME=$(echo "$SECRET_JSON" | jq -r '.BUCKET_NAME')
export AWS_BUCKET_NAME=$(echo "$SECRET_JSON" | jq -r '.BUCKET_NAME')
export MONGODB_PORT=$(echo "$SECRET_JSON" | jq -r '.MONGODB_PORT')
export MONGODB_HOST=$(echo "$SECRET_JSON" | jq -r '.MONGODB_HOST')
export MONGODB_USERNAME=$(echo "$SECRET_JSON" | jq -r '.MONGODB_USERNAME')
export MONGODB_PASSWORD=$(echo "$SECRET_JSON" | jq -r '.MONGODB_PASSWORD')
export MONGODB_DATABASE=$(echo "$SECRET_JSON" | jq -r '.MONGODB_DATABASE')
export MONGODB_AUTHENTICATION_DATABASE=$(echo "$SECRET_JSON" | jq -r '.MONGODB_AUTHENTICATION_DATABASE')

# Create env.list file for Docker
echo "AWS_REGION=$AWS_REGION" >> env.list
echo "AWS_BUCKET_NAME=$BUCKET_NAME" >> env.list
echo "MONGODB_PORT=$MONGODB_PORT" >> env.list
echo "MONGODB_HOST=$MONGODB_HOST" >> env.list
echo "MONGODB_USERNAME=$MONGODB_USERNAME" >> env.list
echo "MONGODB_PASSWORD=$MONGODB_PASSWORD" >> env.list
echo "MONGODB_DATABASE=$MONGODB_DATABASE" >> env.list
echo "MONGODB_AUTHENTICATION_DATABASE=$MONGODB_AUTHENTICATION_DATABASE" >> env.list

# ECR login (replace with your AWS account and region)
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin <your-aws-account-id>.dkr.ecr.$AWS_REGION.amazonaws.com

# Pull the latest Docker image and run the container (replace with your repository)
docker pull <your-aws-account-id>.dkr.ecr.$AWS_REGION.amazonaws.com/<your-repo>:latest
docker run -d --name photo_archival -p 5000:5000 --env-file env.list <your-aws-account-id>.dkr.ecr.$AWS_REGION.amazonaws.com/<your-repo>:latest
