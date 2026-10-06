# AI Working Journal

## Purpose

AI was used as a primary development assistant during the implementation of this assessment.

The AI was used for architecture guidance, API design, implementation assistance, debugging, testing, performance considerations, and documentation.

All AI-generated suggestions and code were reviewed, validated, modified where required, and tested before being included in the final implementation.

---

## 1. Application Architecture

### AI suggestion

Use Clean Architecture with separate API, Application, Domain, and Infrastructure layers.

### Decision

Accepted.

### Reason

The separation keeps business logic independent from REST controllers, database implementation, and framework-specific details.

It also improves maintainability, testability, and future extensibility.

---

## 2. Application Structure

### AI suggestion

Use a modular monolithic Spring Boot application rather than multiple microservices.

### Decision

Accepted.

### Reason

The assessment focuses on a single policy management domain. A modular monolith provides clear separation of responsibilities without adding unnecessary distributed-system complexity.

---

## 3. Technology Stack

### AI suggestion

Use Java 21, Spring Boot, Maven, Spring Data JPA, H2, Flyway, JUnit 5, Mockito, and OpenAPI.

### Decision

Accepted.

### Reason

The selected stack provides a standard approach for developing, testing, documenting, and running a RESTful Java backend.

The application was developed using Eclipse IDE.

---

## 4. API Design

### AI suggestion

Expose the functionality through REST APIs using `/api/v1/policies`.

### Decision

Accepted.

### Reason

REST is appropriate for the synchronous policy retrieval, search, flagging, and summary operations required by the assessment.

API versioning also provides flexibility for future changes.

---

## 5. Contract-First API

### AI suggestion

Create an OpenAPI 3.x specification before implementing the API and use it as the API contract.

### Decision

Accepted.

### Reason

The OpenAPI specification provides a single definition for:

* Endpoints
* Parameters
* Request bodies
* Response schemas
* Enumerations
* Validation rules
* Error responses

The contract is maintained in:

`src/main/resources/openapi/policy-api.yaml`

Swagger UI is configured to use this specification.

---

## 6. Database

### AI suggestion

Use a relational database with Spring Data JPA and Flyway migrations.

### Decision

Accepted.

### Reason

The policy data is structured and requires relational queries, filtering, sorting, pagination, and aggregation.

H2 was selected for local development and assessment execution.

Flyway provides version-controlled database schema migrations.

---

## 7. Database Schema

### AI suggestion

Create a `policies` table containing policy identification, policyholder, business, financial, date, region, underwriter, review, and audit information.

### Decision

Accepted.

### Reason

The schema directly supports the API requirements while maintaining clear relational data types and constraints.

Indexes were added to commonly queried fields such as:

* Status
* Line of business
* Region
* Effective date
* Expiry date

---

## 8. Policy Status

### AI suggestion

Represent policy status using an enum:

```text
ACTIVE
EXPIRED
PENDING
CANCELLED
```

### Decision

Accepted.

### Reason

Using an enum prevents unsupported status values from entering the application.

The API exposes business-friendly values such as `Active`, `Expired`, `Pending`, and `Cancelled`.

---

## 9. Line of Business

### AI suggestion

Represent line of business using an enum:

```text
PROPERTY
CASUALTY
A_AND_H
MARINE
```

### Decision

Accepted.

### Reason

An enum provides controlled and consistent business values while allowing readable API values such as `Property`, `Casualty`, `A&H`, and `Marine`.

---

## 10. Policy Search

### AI suggestion

Perform filtering and free-text search at the database level.

### Decision

Accepted.

### Reason

Database-side filtering avoids retrieving unnecessary records into application memory.

Free-text search supports:

* Policy number
* Policyholder name
* Underwriter

Additional filters include:

* Status
* Line of business
* Region
* Effective date range

---

## 11. Pagination

### AI suggestion

Use Spring Data `Pageable` for pagination.

### Decision

Accepted.

### Reason

The number of policies can grow over time. Returning all policies in a single response would not be scalable.

Pagination limits the number of records processed and returned by each request.

---

## 12. Sorting

### AI suggestion

Allow sorting only on explicitly supported fields.

### Decision

Accepted.

### Reason

Validating sort fields prevents invalid or unexpected database queries.

The application validates both the sort field and sort direction before executing the query.

---

## 13. Bulk Flagging

### AI suggestion

Provide a bulk endpoint:

```text
PATCH /api/v1/policies/flag
```

### Decision

Accepted.

### Reason

The requirement allows multiple policies to be flagged for review.

A bulk endpoint avoids requiring a separate request for every policy.

The implementation verifies that the requested policy IDs exist before updating them.

---

## 14. Policy Summary

### AI suggestion

Use database aggregation queries for policy summary information.

### Decision

Accepted.

### Reason

Aggregation is more efficient when performed by the database instead of loading all policies into application memory.

The summary provides:

* Policy count by status
* Total premium by line of business
* Policies expiring within 30 days

---

## 15. Expiring-Soon Definition

### AI suggestion

Define "expiring soon" as policies whose expiry date falls within the next 30 days.

### Decision

Accepted.

### Reason

The requirement does not specify an exact period.

A 30-day window provides a simple and consistent business definition for the assessment.

---

## 16. Domain and Persistence Separation

### AI suggestion

Keep the domain `Policy` model separate from the JPA `PolicyEntity`.

### Decision

Accepted.

### Reason

This prevents database-specific annotations and persistence concerns from leaking into the domain layer.

A mapper is used between the domain model and persistence entity.

---

## 17. Error Handling

### AI suggestion

Use a centralized exception handler.

### Decision

Accepted.

### Reason

Centralized handling provides consistent HTTP responses and prevents duplicate error-handling logic across controllers.

The implementation handles scenarios such as:

* Policy not found
* Invalid pagination
* Invalid sorting
* Invalid request data

---

## 18. Validation

### AI suggestion

Validate incoming API requests before processing business logic.

### Decision

Accepted.

### Reason

Early validation prevents invalid requests from reaching the service and persistence layers.

For example, the bulk flagging request cannot contain an empty policy ID list.

---

## 19. Testing Strategy

### AI suggestion

Use unit tests for business logic and integration/API tests for end-to-end application behavior.

### Decision

Accepted.

### Tests cover

* Policy retrieval
* Policy not found
* Pagination
* Sorting
* Filtering
* Free-text search
* Bulk flagging
* Missing policy validation
* Policy summary
* Request validation
* Exception handling

JUnit 5 and Mockito are used for automated testing.

---

## 20. Performance

### AI suggestion

Use database-side pagination, filtering, sorting, indexing, and aggregation to meet the performance requirement.

### Decision

Accepted.

### Reason

These techniques reduce unnecessary application processing and database/network overhead.

The target is a p95 response time below 300 ms under 50 concurrent sessions.

JMeter can be used to validate the performance requirement.

---

## 21. API Documentation

### AI suggestion

Provide Swagger UI backed by the OpenAPI contract.

### Decision

Accepted.

### Reason

Swagger provides an interactive interface for reviewing and testing the APIs.

The documentation is available at:

`http://localhost:8080/swagger-ui/index.html`

---

## 22. Health Check

### AI suggestion

Use Spring Boot Actuator for application health monitoring.

### Decision

Accepted.

### Reason

The health endpoint provides a simple way to verify that the application is running.

Endpoint:

`/actuator/health`

---

## 23. Docker

### AI suggestion

Docker could be used to package and run the application.

### Decision

Deferred.

### Reason

Docker was not required for the final assessment.

The final application is designed to run using Java, Maven, H2, and Eclipse.

No Docker files or Docker configuration are included in the Git repository.

---

## 24. Development Environment

### AI suggestion

Use an IDE with Java and Maven support.

### Decision

Accepted.

### Implementation

The application was developed using:

* Eclipse IDE
* Java 21
* Spring Boot
* Maven

Maven is also used for command-line builds and test execution.

---


## 25. Features Deliberately Deferred

The following features were not prioritized:

* Authentication and authorization
* OAuth2/OIDC
* Frontend application
* External system integrations
* Distributed caching
* Kubernetes deployment
* Multi-region deployment
* Production database deployment
* Advanced distributed tracing

### Reason

The focus was on delivering the core backend functionality, clean architecture, API contract, database implementation, testing, and documentation within the assessment scope.

---

## 26. Developer Review

AI-generated suggestions were not accepted blindly.

The implementation was reviewed against:

* Functional requirements
* API contract
* Architecture
* Database consistency
* Validation
* Error handling
* Performance
* Testing
* Maintainability
* Git repository quality

Suggestions were accepted, modified, challenged, or deferred based on their relevance to the assessment.

---
