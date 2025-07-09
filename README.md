# 📊 JSON Group By and Sort By API

A Spring Boot REST API that allows you to dynamically store, group, and sort JSON-formatted records within named datasets. Ideal for flexible data ingestion and querying operations such as employee records, product catalogs, or custom datasets.

---

## 🚀 Features

- ✅ Insert dynamic JSON records into datasets
- 📂 Group records by any field (`groupBy`)
- 🔼🔽 Sort records by any numerical or string field (`sortBy`) in `asc` or `desc` order
- 🧪 Complete unit and controller-level testing using JUnit & Mockito
- 📘 OpenAPI (`Swagger`) documentation support

---

## 📦 Tech Stack

| Tech             | Description          |
|------------------|----------------------|
| Java 17         | Core programming     |
| Spring Boot      | REST API framework   |
| Spring Data JPA  | Database interaction |
|  MySQL       | Embedded / SQL DB    |
| Jackson          | JSON parsing         |
| Mockito + JUnit  | Testing framework    |
| Swagger (Springdoc OpenAPI) | API Docs |

---

## 📁 Project Structure

src/
├── controller/
│ └── DatasetController.java
├── service/
│ └── DatasetServiceImpl.java
├── model/
│ └── DatasetRecord.java
├── repository/
│ └── DatasetRecordRepository.java
├── exception/
│ └── GlobalExceptionHandler.java
├── test/
│ └── DatasetControllerTest.java


## 📥 API Endpoints

### 📌 Insert Record

- **POST** `/api/dataset/{datasetName}/record`
- Inserts a JSON record into a dataset.

#### 👉 Example Request

```json
POST /api/dataset/employees/record
{
  "name": "Rajkumar prasad",
  "age": 30,
  "department": "Engineering"
}

#### 👉 Example Response

{
  "message": "Record added successfully",
  "dataset": "student_dataset",
  "recordId": 1
}

#### 👉 Example Request
Group By Field
GET /api/dataset/{datasetName}/query?groupBy=department

#### 👉 Example Response

{
  "groupedRecords": {
    "HR": [
      { "name": "Alice", "department": "HR" }
    ],
    "IT": [
      { "name": "Bob", "department": "IT" }
    ]
  }
}

#### 👉 Example Request
GET /api/dataset/{datasetName}/query?sortBy=age&order=desc

#### 👉 Example Response
{
 "sortedRecords": [
 { "id": 2, "name": "Jane Smith", "age": 25, "department": 
"Engineering" },
 { "id": 3, "name": "Alice Brown", "age": 28, "department": 
"Marketing" },
 { "id": 1, "name": "John Doe", "age": 30, "department": 
"Engineering" }
 ]
 }

-------------------------------------------------------------------------------
#### 👉   Error Handling
If neither sortBy nor groupBy is provided:

{
  "message": "Provide either groupBy or sortBy",
  "status": 400
}
NOTE : All exceptions are handled by GlobalExceptionHandler.java
----------------------------------------------------------------

 #### 👉 Running Tests

Run all tests via:
# Maven
./mvnw test

# Or Gradle
./gradlew test

----------------------------------------------
 #### 👉  API Docs
      Browse all available endpoints using Swagger UI:

      http://localhost:8080/swagger-ui/index.html




