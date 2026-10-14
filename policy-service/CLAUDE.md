# Policy Service Project Memory (CLAUDE.md)

## Project Goal

Build a production-grade Policy Management Backend Service using Contract-First API Design and Clean Architecture principles.

The service manages insurance policy data and provides APIs for:

- Policy listing with filtering, search, sorting, and pagination
- Policy retrieval by ID
- Bulk flagging policies for review
- Portfolio summary statistics

The implementation should reflect enterprise-grade engineering standards expected in a production environment.

---
# Stack (Do Not Change)

## Core

- Java 21
- Maven

Use the Spring Boot version already defined in `pom.xml`.

Never change Spring Boot or dependency versions unless explicitly requested.

## Spring Modules

- Spring Boot Starter Web
- Spring Boot Starter Validation
- Spring Boot Starter Data JPA
- Spring Boot Starter Actuator

## Contract First

- OpenAPI 3.x
- OpenAPI Generator Maven Plugin

## Database

Preferred:

- H2

Schema Management:

- Flyway

## Testing

- JUnit 5
- Mockito
- AssertJ
- MockMvc
- RestAssured
- Testcontainers

Coverage:

- JaCoCo
- Line Coverage >= 80%
- Coverage enforced during `verify`

## Java Rules

Do not use Lombok.

Use Java `record` for DTOs where appropriate.

Use:

```java
BigDecimal
```

for monetary values.

Use:

```java
LocalDate
```

for business dates.

Use:

```java
Instant
```

for timestamps.

Persist enums using:

```java
@Enumerated(EnumType.STRING)
```

---

# Contract-First Development Rules (Non-Negotiable)

1. OpenAPI specification is the source of truth.
2. Contract changes happen before code changes.
3. Controllers implement generated interfaces.
4. Request and response models originate from OpenAPI.
5. Generated code must never be manually modified.
6. Build must fail if generated code is out of sync with the contract.
7. API-first development must be followed throughout the project.

---

# Architecture (Must Follow)

Follow Clean Architecture principles.

Dependency direction must always point inward.

```text
API
 ↓
Application
 ↓
Domain

Infrastructure implements Domain contracts
```

Infrastructure concerns must never leak into Domain or Application layers.

---

# Package Layout

Base package:

```text
com.prathyusha.chubb.policy
```

---

# Layer Responsibilities

## API Layer

Responsibilities:

- Request validation
- Response generation
- Controller orchestration
- API mapping

Must not contain:

- Business logic
- Repository calls

## Application Layer

Responsibilities:

- Business use cases
- Service orchestration
- Transaction boundaries
- Business rules

## Domain Layer

Responsibilities:

- Domain model
- Domain rules
- Repository contracts
- Domain enums

Must not depend on:

- Spring
- JPA
- Flyway
- Cache implementations

## Infrastructure Layer

Responsibilities:

- Database implementation
- JPA repositories
- Specifications
- External systems

---

# Repository Rules

Domain repository interfaces belong under:

```text
domain.repository
```

Spring Data repositories belong under:

```text
infrastructure.persistence.repository
```

Application and Domain layers must depend only on repository interfaces.

---

# Commands

## Build + Test + Coverage

```bash
./mvnw -q -B verify
```

Windows:

```bash
mvnw.cmd -q -B verify
```

## Run Application

```bash
./mvnw spring-boot:run
```

## Run Single Test

```bash
./mvnw -q test -Dtest=PolicyServiceTest
```

## Generate OpenAPI Sources

```bash
./mvnw generate-sources
```

---

# OpenAPI Rules

OpenAPI specification is the single source of truth.

Location:

```text
openapi/policy-api.yaml
```

Rules:

1. Define or update OpenAPI first.
2. Generate interfaces and DTOs from OpenAPI.
3. Controllers implement generated interfaces.
4. Generated sources belong under:

```text
api.generated
```

5. Never manually edit generated code.
6. Custom code must never be placed in generated packages.

---

# REST API

Base Path:

```text
/api/v1
```

Required Endpoints:

```text
GET     /api/v1/policies

GET     /api/v1/policies/{id}

PATCH   /api/v1/policies/flag

GET     /api/v1/policies/summary
```

---

# GET /api/v1/policies

Supports:

- Pagination
- Sorting
- Filtering
- Search

Query Parameters:

```text
page
size
sort
status
lineOfBusiness
region
effectiveDateFrom
effectiveDateTo
search
```

Example:

```http
GET /api/v1/policies?page=0
&size=20
&sort=premiumAmount,desc
&status=ACTIVE
&lineOfBusiness=PROPERTY
&region=Singapore
&search=Tan
```

---

# GET /api/v1/policies/{id}

Retrieve a policy by ID.

Responses:

```text
200 OK
404 Not Found
```

---

# PATCH /api/v1/policies/flag

Bulk flag policies for review.

Request:

```json
{
  "policyIds": [
    "uuid-1",
    "uuid-2",
    "uuid-3"
  ]
}
```

Response:

```json
{
  "flaggedCount": 3
}
```

---

# GET /api/v1/policies/summary

Returns:

- Counts by status
- Total premium by line of business
- Expiring soon count

Example:

```json
{
  "statusCounts": {
    "ACTIVE": 120,
    "EXPIRED": 40,
    "PENDING": 25,
    "CANCELLED": 15
  },
  "premiumByLineOfBusiness": {
    "PROPERTY": 25000000,
    "CASUALTY": 17000000,
    "AH": 9000000,
    "MARINE": 12000000
  },
  "expiringSoonCount": 15
}
```

Summary statistics must be calculated through database aggregation queries.

Do not load all policies into memory to compute summary data.

---

# Pagination Rules

Default:

```text
page=0
size=20
```

Maximum:

```text
size=100
```

Use:

```java
Pageable
```

Never return unbounded collections.

Pagination must occur at the database layer.

---

# Sorting Rules

Supported fields only:

```text
policyNumber
policyholderName
premiumAmount
effectiveDate
expiryDate
status
```

Reject unsupported sort fields.

Return:

```text
400 Bad Request
```

Example:

```text
sort=premiumAmount,desc
```

---

# Filtering Rules

Supported filters:

```text
status
lineOfBusiness
region
effectiveDateFrom
effectiveDateTo
```

All filters must be composable.

---

# Search Rules

Free-text search must be case-insensitive.

Search across:

```text
policyNumber
policyholderName
underwriter
```

Preferred implementation:

```text
JPA Specification
```

Filtering and searching must be implemented using dynamic specifications.

Do not create repository methods for every possible filter combination.

---

# Domain Model

## PolicyStatus

```java
ACTIVE
EXPIRED
PENDING
CANCELLED
```

## LineOfBusiness

```java
PROPERTY
CASUALTY
AH
MARINE
```

## Policy Fields

```text
id
policyNumber
policyholderName
lineOfBusiness
status
premiumAmount
currency
effectiveDate
expiryDate
region
underwriter
flaggedForReview
createdAt
updatedAt
```

---

# DTO Naming Rules

Request DTOs:

```text
CreatePolicyRequest
UpdatePolicyRequest
BulkFlagPoliciesRequest
```

Response DTOs:

```text
PolicyResponse
PolicySummaryResponse
PagedPolicyResponse
```

Avoid generic names:

```text
RequestDTO
ResponseDTO
DataDTO
```

---

# Mapper Rules

Use dedicated mapper classes.

Mapping responsibilities:

- Entity → DTO
- DTO → Entity

Do not place mapping logic:

- Inside controllers
- Inside entities
- Inside repositories

Mapper methods must have unit tests.

---

# Coding Rules (MUST)

1. Constructor injection only.
2. Never use field injection.
3. Never expose JPA entities from APIs.
4. Always use Request DTOs and Response DTOs.
5. Controllers must not contain business logic.
6. Services own business rules.
7. Repository layer owns persistence.
8. Domain layer must remain framework-independent.
9. Use immutable DTOs.
10. No wildcard imports.
11. No dead code.
12. Keep methods generally under 30 lines.
13. Use meaningful names.
14. Prefer explicit code over magic.
15. Favor readability and maintainability.

Never use:

```java
@Autowired
```

on fields.

---

# Transaction Rules

Read operations:

```java
@Transactional(readOnly = true)
```

Write operations:

```java
@Transactional
```

Bulk flagging must execute within a single transaction.

---

# Database Rules

Use Flyway for all schema changes.

Never rely on:

```text
ddl-auto=create
ddl-auto=update
```

Migration naming:

```text
V1__create_policy_table.sql
V2__seed_policy_data.sql
```

Every schema change must be versioned.

---

# Database Schema

Table:

```sql
policies
```

Columns:

```text
id UUID PRIMARY KEY
policy_number VARCHAR(50) UNIQUE
policyholder_name VARCHAR(255)
line_of_business VARCHAR(50)
status VARCHAR(50)
premium_amount DECIMAL(18,2)
currency VARCHAR(10)
effective_date DATE
expiry_date DATE
region VARCHAR(100)
underwriter VARCHAR(255)
flagged_for_review BOOLEAN
created_at TIMESTAMP
updated_at TIMESTAMP
```

---

# Database Indexes

Required:

```sql
CREATE INDEX idx_status
ON policies(status);

CREATE INDEX idx_region
ON policies(region);

CREATE INDEX idx_effective_date
ON policies(effective_date);

CREATE INDEX idx_policy_number
ON policies(policy_number);
```

---

# Seed Data Requirements

Minimum:

```text
200+ policies
```

Must include:

Statuses:

```text
ACTIVE
EXPIRED
PENDING
CANCELLED
```

Regions:

```text
Singapore
Hong Kong
Australia
Japan
Thailand
Indonesia
Malaysia
Philippines
```

Currencies:

```text
USD
SGD
HKD
AUD
JPY
THB
```

Premium range:

```text
1,000 - 5,000,000
```

Policy number format:

```text
POL-XXXXXX
```

Seed data must support:

- Search testing
- Sorting testing
- Pagination testing
- Summary calculations
- All filter combinations

---

# Error Handling

Use a single:

```java
@RestControllerAdvice
```

Return:

```java
ProblemDetail
```

Handle:

## Validation Errors

```text
400 Bad Request
```

Include field-level validation details.

## Resource Not Found

```text
404 Not Found
```

## Conflict Errors

```text
409 Conflict
```

## Internal Errors

```text
500 Internal Server Error
```

Never expose stack traces.

---

# Logging Rules

Use:

```java
SLF4J
```

only.

Never use:

```java
System.out.println()
printStackTrace()
```

Log:

```text
requestId
endpoint
statusCode
duration
```

Prefer structured logging.

Never log:

- Credentials
- Secrets
- Connection strings
- Sensitive data

---

# Health Checks

Expose:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

Mandatory:

```text
Database Health
```

---

# Performance Requirements

Concurrent Load:

```text
50 users
```

Targets:

```text
GET /policies:
P95 < 300ms
```

```text
GET /policies/{id}:
P95 < 300ms
```

```text
Error Rate < 1%
```

Performance test results must be documented in README.

---

# Query Optimization Rules

Required:

- Database pagination
- Indexed filtering
- Efficient sorting
- Specification-driven querying

Avoid:

- N+1 queries
- In-memory filtering
- Full table scans where possible

Use projections when beneficial.

---

# Testing Rules

## Service Tests

Use:

```text
JUnit 5
Mockito
AssertJ
```

Cover:

- Happy path
- Validation failures
- Not found scenarios
- Business rule failures

Naming convention:

```text
methodName_condition_expectedResult
```

Examples:

```text
getPolicy_validId_returnsPolicy

getPolicy_invalidId_throwsNotFoundException

flagPolicies_existingPolicies_returnsFlagCount
```

---

## Controller Tests

Use:

```java
@WebMvcTest
```

Use:

```java
@MockitoBean
```

Verify:

- Request validation
- Response payloads
- Status codes
- Error responses

---

## Repository Tests

Use:

```java
@DataJpaTest
```

Verify:

- Specifications
- Search functionality
- Filtering
- Sorting
- Pagination

---

## Integration Tests

Use:

```text
Testcontainers
```

Validate:

- Flyway migrations
- Database integration
- Endpoint behavior
- End-to-end workflows

---

## Contract Tests

Verify implementation matches the OpenAPI specification.

Generated interfaces remain authoritative.

---

## Performance Tests

Use:

```text
k6
```

or

```text
JMeter
```

Capture:

```text
p50
p95
p99
throughput
error rate
```

Store scripts under:

```text
performance-tests
```

---

# README Requirements

README.md must contain:

- Project overview
- Architecture overview
- Package structure
- Build instructions
- Run instructions
- OpenAPI generation instructions
- Flyway migration details
- API examples
- Curl commands for all endpoints
- Testing instructions
- Performance testing instructions
- Design decisions
- Trade-offs

---

# Deliverables

Repository must contain:

```text
README.md

CLAUDE.md

openapi/policy-api.yaml

Flyway migrations

Seed data scripts

Unit tests

Integration tests

Performance test scripts
```

---

# Definition of Done

✅ OpenAPI contract exists and drives implementation

✅ Generated interfaces used by controllers

✅ All endpoints functional

✅ Search works

✅ Filtering works

✅ Sorting works

✅ Pagination works

✅ Flyway migrations applied

✅ 200+ records seeded

✅ Clean Architecture maintained

✅ Unit tests passing

✅ Integration tests passing

✅ Coverage >= 80%

✅ Actuator endpoints available

✅ Global exception handling implemented

✅ Structured logging enabled

✅ Performance target demonstrated

✅ README contains setup instructions

✅ README contains curl examples for every endpoint

✅ Local development setup documented

---

# Do NOT

- Do not expose JPA entities through APIs.
- Do not place business logic inside controllers.
- Do not bypass OpenAPI-generated contracts.
- Do not modify generated source files.
- Do not use field injection.
- Do not use Lombok.
- Do not hardcode configuration values.
- Do not perform filtering in memory when database filtering is possible.
- Do not return unbounded collections.
- Do not use Hibernate auto-ddl as a migration strategy.
- Do not leak infrastructure concerns into Domain.
- Do not commit secrets or credentials.
- Do not modify dependency versions without approval.
- Do not break Clean Architecture boundaries.
- Do not introduce unnecessary frameworks.