# Implementation Notes

## 1. Technology Decisions

The application uses Java 21 and Spring Boot for implementing the backend REST service.

H2 is used as the relational database for local development and testing.

Flyway is used to manage database schema creation and seed data.

## 2. API Design

The API follows REST principles and uses versioning through:

```text
/api/v1
```

The OpenAPI specification is maintained separately and acts as the API contract.

OpenAPI file:

```text
src/main/resources/openapi/policy-api.yaml
```

## 3. Database

The `policies` table contains:

* Policy ID
* Policy number
* Policyholder
* Line of business
* Status
* Premium amount
* Currency
* Effective date
* Expiry date
* Region
* Underwriter
* Review flag
* Created timestamp
* Updated timestamp

Indexes are provided for commonly filtered fields such as status, line of business, region, effective date, and expiry date.

## 4. Policy Search

The policy listing API supports:

* Pagination
* Sorting
* Status filtering
* Line of business filtering
* Region filtering
* Effective date filtering
* Free-text search

Free-text search covers:

* Policy number
* Policyholder name
* Underwriter

## 5. Error Handling

A centralized exception handler is used for consistent API error responses.

For example, when a policy does not exist, the API returns HTTP 404.

## 6. Validation

Request validation is applied to API inputs.

For bulk flagging, the policy ID list cannot be empty.

Pagination parameters are validated to prevent invalid page and size values.

## 7. Testing

Unit tests use JUnit 5 and Mockito.

Testing focuses on business logic, validation, error handling, filtering, sorting, and policy operations.

Integration/API tests are used to verify interaction between the REST layer and persistence layer.

## 8. Performance Considerations

The policy search API uses database-side filtering, sorting, and pagination instead of loading the complete dataset into application memory.

Database indexes are added to commonly queried fields.

The target performance requirement is a p95 response time below 300 ms under 50 concurrent sessions.

## 9. Assumptions

* H2 is sufficient for local development and automated testing.
* Production deployment can use a supported enterprise relational database such as SQL Server.
* Policy UUIDs are generated as unique identifiers.
* Policies expiring within the next 30 days are considered "expiring soon".
* Premium values use two decimal places.
* API responses expose business-friendly enum values such as `Active` and `Property`.
