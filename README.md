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

## Installation and Usage

1. Clone the repository:
   ```bash
   git clone https://github.com/thalesmaiaa/photoarchival.git
   ```
2. Navigate to the project directory:
   ```bash
   cd photoarchival
   ```
3. Run Docker containers

   ```bash
   docker compose up -d
   ```

   4 . Build the project using Maven:

   ```bash
   mvn clean install
   ```

4. Start the application:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

Don't forget to set up your MongoDB connection in the `application.yml` file.

## More details about CI/CD [here](deployment.md)

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
