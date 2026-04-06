# FeedRight Backend

Spring Boot backend service for the FeedRight application (Kotlin).

## Tech Stack

- **Language:** Kotlin
- **Framework:** Spring Boot 3.2.4
- **Database:** PostgreSQL 14+
- **ORM:** Spring Data JPA + Hibernate
- **Security:** Spring Security (simplified for MVP)
- **Build Tool:** Gradle (Kotlin DSL)

## Prerequisites

- Java 17+
- Docker & Docker Compose
- Gradle (or use the wrapper: `./gradlew`)

## Quick Start

### 1. Start PostgreSQL Database

From the project root directory (`feedright/`):

```bash
docker-compose up -d
```

This starts PostgreSQL on `localhost:5432` with:
- Database: `feedright`
- User: `feedright_user`
- Password: `feedright_pass`

### 2. Run the Application

```bash
./gradlew bootRun
```

Or build and run:

```bash
./gradlew build
java -jar build/libs/feedright-backend-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

### 3. Test Data

On first startup, the application automatically seeds the database with:
- **5 salesmen** (Territory: North, South, East District)
- **20 farms** (Distributed across territories)
- **23 products** (20 active pig products, 3 inactive cattle products)

**Test credentials:**
- Phone: `+1234567890`
- PIN: `1234`

## API Endpoints

### Authentication

```
POST /api/auth/login
Body: { "phone": "+1234567890", "pin": "1234" }
Response: { "token": "Bearer-xxx", "salesman": {...} }
```

### Visits

```
GET    /api/visits                    # Get all visits
GET    /api/visits/salesman/{id}      # Get visits by salesman
GET    /api/visits/range?start=&end=  # Get visits by date range
POST   /api/visits                    # Create single visit
POST   /api/visits/sync               # Batch sync visits from mobile
```

### Farms

```
GET    /api/farms                     # Get all farms
GET    /api/farms/{id}                # Get farm by ID
GET    /api/farms/territory/{name}    # Get farms by territory
POST   /api/farms                     # Create new farm
```

### Products

```
GET    /api/products                  # Get all products
GET    /api/products?activeOnly=true  # Get active products only
GET    /api/products/{id}             # Get product by ID
GET    /api/products/category/{cat}   # Get products by category (e.g., "pig")
```

### Salesmen

```
GET    /api/salesmen                  # Get all salesmen
GET    /api/salesmen/{id}             # Get salesman by ID
GET    /api/salesmen/territory/{name} # Get salesmen by territory
```

## Database Schema

### Entities

- **Salesman**: id, name, phone, territory, pin (hashed)
- **Farm**: id, name, location, territory
- **Product**: id, sku, name, category, is_active
- **Visit**: id, salesman_id, farm_id, product_id, quantity, visit_date, notes, device_id, synced_at, created_at

### Relationships

- Visit → Salesman (Many-to-One)
- Visit → Farm (Many-to-One)
- Visit → Product (Many-to-One)

## Development

### Build

```bash
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

### Clean Build

```bash
./gradlew clean build
```

## Configuration

Configuration is in `src/main/resources/application.yml`:

- Server port: `8080`
- Database URL: `jdbc:postgresql://localhost:5432/feedright`
- JWT secret: (change in production!)
- Hibernate DDL: `update` (creates/updates tables automatically)

## Offline Sync Logic

The `/api/visits/sync` endpoint handles batch uploads from mobile devices:

1. Accepts array of visit records
2. Uses `device_id + created_at` for idempotency (prevents duplicates)
3. Returns success/failure count
4. Stamps each visit with `synced_at` timestamp

## Next Steps

- [ ] Add proper JWT implementation (currently using simple bearer tokens)
- [ ] Add input validation with Bean Validation
- [ ] Add comprehensive error handling
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Add integration tests
- [ ] Add database migrations (Flyway or Liquibase)
