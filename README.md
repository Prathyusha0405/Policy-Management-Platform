# Policy Management Service

Spring Boot 4 REST API for insurance policy data: list/search, get by id, bulk flag for review, portfolio summary.
Contract-first (`openapi/policy-api.yaml`), Clean Architecture, H2 (in-memory) + Flyway, 240 seeded policies.

## Layout (`com.hcl.prathyusha.policyservice`)

| Package | Responsibility |
|---|---|
| `api` | Controller implementing the **generated** `PoliciesApi`, API mapper (`api.generated` is build output, never edited) |
| `application` | `PolicyService` — use cases, transactions, sort/date validation |
| `domain` | Framework-free models, enums, `PolicyRepository` contract |
| `infrastructure` | JPA entity, Spring Data repo, `PolicySpecifications`, repository adapter, config |
| `common` | `GlobalExceptionHandler` (RFC 9457 `ProblemDetail`), request logging filter |

## Build / run / test

Needs a JDK 21+ (`JAVA_HOME` set).

```bash
./mvnw -B verify          # generate sources, test, JaCoCo (>= 80% line coverage enforced)
./mvnw spring-boot:run    # http://localhost:8080
./mvnw generate-sources   # regenerate API interfaces/models from the OpenAPI contract
```

Windows: `mvnw.cmd`.

## Endpoints

```bash
# list (page, size<=100, sort=field,dir, status, lineOfBusiness, region, effectiveDateFrom/To, search)
curl "http://localhost:8080/api/v1/policies?page=0&size=20&sort=premiumAmount,desc&status=ACTIVE&lineOfBusiness=PROPERTY&region=Singapore&search=Tan"

# by id (404 problem+json if unknown)
curl http://localhost:8080/api/v1/policies/<uuid>

# summary: counts by status, premium by line of business, active policies expiring in the next 30 days
curl http://localhost:8080/api/v1/policies/summary

# bulk flag (1-500 ids; unknown ids are ignored, response is the number actually flagged)
curl -X PATCH http://localhost:8080/api/v1/policies/flag -H "Content-Type: application/json" \
     -d '{"policyIds":["<uuid-1>","<uuid-2>"]}'

curl http://localhost:8080/actuator/health
```

Sortable fields: `policyNumber, policyholderName, premiumAmount, effectiveDate, expiryDate, status`; anything else → `400`.

## Design notes / trade-offs

- Filtering, search, sorting and paging run in the database (JPA Specification, `Pageable`); summary uses `GROUP BY` queries.
- Schema is owned by Flyway (`V1` table + indexes, `V2` seed); Hibernate runs with `ddl-auto=validate`.
- Seed dates are relative to the day the app starts, so "expiring soon" always has data.
- Premium totals are summed per line of business **without currency conversion** (seed data mixes currencies).
- Not included yet: Testcontainers/RestAssured tests (H2 is used directly), k6/JMeter performance scripts.
 
