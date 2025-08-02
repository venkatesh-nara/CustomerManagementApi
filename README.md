# Customer Management API

## How to Run
```bash
mvn spring-boot:run
```

## Endpoints
- POST /customers
- GET /customers/{id}
- GET /customers?name=...
- GET /customers?email=...
- PUT /customers/{id}
- DELETE /customers/{id}

## Access H2 Console
Visit: `http://localhost:8080/h2-console`

JDBC URL: `jdbc:h2:mem:testdb`
User name: sa
password: leave blank

## Sample POST Request
```json
{
  "name": "Peter Han",
  "email": "Peter@gmail.com",
  "annualSpend": 2000.0,
  "lastPurchaseDate": "2025-06-01T10:00:00"
}
```

## Assumptions
- Tier is calculated during GET/PUT responses.
- ID must not be included in POST requests.

## Run Tests
```bash
mvn test
```