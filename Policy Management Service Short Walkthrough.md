# Policy Management Service – Walkthrough

## 1. Overview

The Policy Management Service is a RESTful backend application developed using **Java and Spring Boot**.

The application provides APIs for:

* Searching policies
* Retrieving individual policies
* Filtering and sorting policies
* Flagging policies for review
* Viewing policy summary statistics

## 2. Development Environment

The application was developed using:

* Java 21
* Spring Boot
* Maven
* Eclipse IDE
* H2 Database
* Spring Data JPA
* Flyway
* OpenAPI / Swagger
* JUnit 5
* Mockito

## 3. Application Startup

### Prerequisites

* Java 21
* Maven
* Eclipse IDE

### Run from Eclipse

Open the project in Eclipse and run:

```text
PolicyServiceApplication.java
```

as a **Spring Boot App**.

The application starts at:

```text
http://localhost:8080
```

### Run using Maven

```bash
mvn clean package
mvn spring-boot:run
```

## 4. API Walkthrough

### Step 1 – List Policies

```text
GET /api/v1/policies
```

Supports pagination, sorting, filtering, and free-text search.

Example:

```text
GET /api/v1/policies?page=0&size=20&sort=premiumAmount,desc
```

### Step 2 – Get a Policy

```text
GET /api/v1/policies/{id}
```

Returns complete details for the requested policy.

### Step 3 – Flag Policies

```text
PATCH /api/v1/policies/flag
```

Accepts multiple policy IDs and flags them for review.

### Step 4 – View Summary

```text
GET /api/v1/policies/summary
```

Returns:

* Counts by policy status
* Total premium by line of business
* Policies expiring within 30 days

## 5. Swagger

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The APIs can be explored and tested directly from Swagger UI.

## 6. Database

The application uses H2 with Flyway migrations.

Database scripts are located at:

```text
src/main/resources/db/migration/
```

The schema and seed data are created automatically when the application starts.

## 7. Testing

Tests can be executed from Eclipse or using Maven.

```bash
mvn clean test
```

The test suite covers service logic, API behavior, validation, exception handling, filtering, pagination, sorting, and database interactions.

## 8. Health Check

The application health endpoint is:

```text
http://localhost:8080/actuator/health
```
