# Photo Archival API Documentation

## Endpoints

### 1. Upload Media

- **POST** `/medias/upload`
- **Description:** Upload a new media file.
- **Request Body:**
  ```json
  {
    "fileName": "string",
    "folderName": "string",
    "file": "data:<mime-type>;base64,<base64-encoded-content>"
  }
  ```
- **Response:** `201 Created` (no body)

---

### 2. List/Filter Medias

- **GET** `/medias`
- **Description:** List all media items, optionally filtered by query parameters.
- **Query Parameters (Filters):**
  - `fileName` (string)
  - `folderName` (string)
  - `category` (string)
  - `gender` (string)
  - `dominantEmotion` (string)
  - `mustache` (boolean)
  - `beard` (boolean)
  - `labelName` (string)
  - `smile` (boolean)
  - `page` (integer, optional)
  - `size` (integer, optional)
- **Response:**
  ```json
  {
    "content": [
      {
        "id": "string",
        "fileName": "string",
        "folderName": "string",
        "url": "string",
        "uploadedAt": "ISO8601 date",
        "metadata": { ... }
      }
    ],
    "totalElements": integer,
    "totalPages": integer,
    "size": integer,
    "number": integer
  }
  ```

---

### 3. Update Media Metadata (endpoint called by AWS Lambda [Photo Archival Lambda](https://github.com/thalesmaiaa/photo-archival-lambda))

- **PATCH** `/medias/{requestFile}/metadata`
- **Description:** Update metadata for a specific media file.
- **Request Body:**
  ```json
  {
    // Metadata fields provided by Amazon Rekognition face  and label detection analysis
  }
  ```
- **Response:** `204 No Content`

---

## Filters Reference

You can filter media items using the following query parameters:

| Parameter       | Type    | Description                         |
| --------------- | ------- | ----------------------------------- |
| fileName        | string  | Filter by file name                 |
| folderName      | string  | Filter by folder name               |
| category        | string  | Filter by label category            |
| gender          | string  | Filter by detected gender           |
| dominantEmotion | string  | Filter by dominant detected emotion |
| mustache        | boolean | Filter by presence of mustache      |
| beard           | boolean | Filter by presence of beard         |
| labelName       | string  | Filter by label name                |
| smile           | boolean | Filter by presence of smile         |

---

## Example: Filter by fileName and mustache

```
GET /medias?fileName=holiday&mustache=true
```

---

For more details, see the controller and DTO classes in the codebase.
