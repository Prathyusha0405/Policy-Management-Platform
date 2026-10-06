# Policy Management Service

## Overview

Policy Management Service is a RESTful backend application built using **Java and Spring Boot**.

The service provides APIs to search, retrieve, flag, and summarize insurance policies.

### Key Features

* Policy retrieval and search
* Pagination and sorting
* Filtering by status, line of business, region, and effective date
* Free-text search
* Bulk policy flagging for review
* Policy summary and aggregation
* OpenAPI 3 contract
* H2 relational database
* Flyway database migrations
* Global exception handling
* Health monitoring using Spring Boot Actuator
* Automated testing

## Software and Technology

* **Programming Language:** Java 21
* **Framework:** Spring Boot
* **Build Tool:** Maven
* **IDE:** Eclipse IDE or InteliJ IDE
* **Database:** H2
* **Database Migration:** Flyway
* **Persistence:** Spring Data JPA
* **API Documentation:** OpenAPI / Swagger
* **Testing:** JUnit 5 and Mockito

## Setup

### Prerequisites

* Java 21
* Maven
* Eclipse IDE

### Import the Project in Eclipse

1. Open Eclipse.
2. Select **File → Import**.
3. Select **Maven → Existing Maven Projects**.
4. Select the `policy-service` project directory.
5. Click **Finish**.
6. Allow Maven dependencies to download.

### Run the Application

The application can be started from Eclipse by running the main Spring Boot application class:

```text
com.prathyusha.chubb.policy.PolicyServiceApplication
```

Alternatively, run from the terminal:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## API Reference

### List Policies

```text
GET /api/v1/policies
```

Supports pagination, sorting, filtering, and free-text search.

### Get Policy

```text
GET /api/v1/policies/{id}
```

Returns a policy by its UUID.

### Flag Policies

```text
PATCH /api/v1/policies/flag
```

Flags multiple policies for review.

### Policy Summary

```text
GET /api/v1/policies/summary
```

Returns policy counts, premium totals by line of business, and expiring-soon policies.

## API Documentation

OpenAPI contract:

```text
src/main/resources/openapi/policy-api.yaml
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Database

H2 is used as the relational database for local development and testing.

Flyway manages database schema creation and seed data.

Migration files:

```text
src/main/resources/db/migration/
```

## Testing

Run all tests using Maven:

```bash
mvn clean test
```

Tests cover:

* Application/service logic
* REST API behavior
* Validation
* Exception handling
* Policy filtering
* Pagination and sorting
* Database interactions

## Project Documentation

Additional documentation is available in:

* `NOTES.md` – implementation notes and assumptions
* `Policy Management Service Short Walkthrough.md` – short application walkthrough
* `ARCHITECTURE.md` – architecture and design details
