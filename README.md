# Movie Ticket Booking Platform

A production-quality online movie ticket booking platform built with Java 17, Spring Boot, and PostgreSQL.

## 🎯 Overview

This is a comprehensive backend service for an online movie ticket booking platform supporting:
- **B2C (End Users)**: Browse shows, book tickets, apply discounts
- **B2B (Theatre Partners)**: Manage theatres, screens, and shows

## 🏗️ Architecture

### Clean Layered Architecture
```
Controller Layer → Service Layer → Repository Layer → Database Layer
```

### Key Design Patterns
- **Strategy Pattern**: Discount calculation engine
- **Repository Pattern**: Data access abstraction
- **Builder Pattern**: DTO construction
- **Optimistic Locking**: Concurrency control

## 🔑 Key Features

### 1. Browse Shows API
- Search shows by city, movie, and date
- View available seats in real-time
- Optimized queries with JOIN FETCH to avoid N+1 problems

### 2. Booking API
- Book tickets with seat selection
- **Concurrency Handling**: Multi-layered approach
  - Optimistic locking (`@Version` annotation)
  - Database unique constraints
  - Application-level validation
- Transaction management for booking flow
- Payment simulation

### 3. Discount Engine
- Strategy Pattern for extensible discount rules
- Implemented discounts:
  - 50% off on 3rd ticket
  - 20% off for afternoon shows (12 PM - 3 PM)


## 📦 Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: Hibernate
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Lombok**: Code generation

## 🗄️ Database Schema

### Entity Relationships

```
User (1) ──────< (N) Booking
Movie (1) ──────< (N) Show
Theatre (1) ────< (N) Screen
Screen (1) ─────< (N) Seat
Screen (1) ─────< (N) Show
Show (1) ───────< (N) Booking
Booking (1) ────< (N) BookingSeat
Seat (1) ───────< (N) BookingSeat
Show (1) ───────< (N) BookingSeat
```


## 🚀 API Endpoints

### 1. Browse Shows

**Endpoint**: `GET /api/v1/shows`

**Query Parameters**:
- `city` (required): City name
- `movieId` (required): Movie ID
- `date` (required): Date in `yyyy-MM-dd` format

**Example Request**:
```
GET /api/v1/shows?city=Mumbai&movieId=1&date=2024-03-25
```

**Response**:
```json
{
  "movie": "Inception",
  "city": "Mumbai",
  "shows": [
    {
      "theatreId": "1",
      "theatreName": "PVR Cinemas",
      "showId": "101",
      "time": "14:30",
      "availableSeats": 120
    }
  ]
}
```

### 2. Create Booking

**Endpoint**: `POST /api/v1/bookings`

**Request Body**:
```json
{
  "userId": "1",
  "showId": "101",
  "seats": ["A1", "A2", "A3"],
  "paymentMethod": "CARD"
}
```

**Response**:
```json
{
  "bookingId": "1",
  "status": "CONFIRMED",
  "amount": 450.00,
  "discount": 50.00,
  "seats": ["A1", "A2", "A3"]
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input
- `404 Not Found`: Show or user not found
- `409 Conflict`: Seats already booked
- `500 Internal Server Error`: Payment failure

## 🔄 Transaction Flow

### Booking Transaction

```
1. Validate user and show
2. Validate seat selection
3. Check seat availability (application-level)
4. Create booking with PENDING status
5. Create booking_seat records (locks seats)
6. Calculate discounts
7. Process payment (mock)
8. Update booking to CONFIRMED
9. Commit transaction
```

**Rollback Scenarios**:
- Payment failure → Entire transaction rolled back
- Optimistic lock exception → Transaction rolled back, user notified
- Database constraint violation → Transaction rolled back

## 💰 Discount Rules

### 1. Third Ticket Discount
- **Rule**: 50% off on the 3rd ticket
- **Condition**: Booking has 3 or more seats
- **Calculation**: 50% discount on the cheapest seat

### 2. Afternoon Show Discount
- **Rule**: 20% off for afternoon shows
- **Condition**: Show time between 12:00 PM and 3:00 PM
- **Calculation**: 20% discount on total amount

### Extensibility
Adding new discounts is easy:
1. Create new class implementing `DiscountStrategy`
2. Annotate with `@Component`
3. Spring automatically registers it

## ⚙️ Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/moviebooking
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```
## 🚀 Running the Application

### Prerequisites
- Java 11+
- PostgreSQL 12+
- Maven 3.6+

### Steps

1. **Clone the repository**
```bash
cd movie-booking-platform
```

2. **Create database**
```sql
CREATE DATABASE moviebooking;
```

3. **Update application.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/moviebooking
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Run schema and data scripts**
```bash
psql -U postgres -d moviebooking -f src/main/resources/db/schema.sql
psql -U postgres -d moviebooking -f src/main/resources/db/data.sql
```

5. **Build the project**
```bash
mvn clean install
```

6. **Run the application**
```bash
mvn spring-boot:run
```

Application will start on `http://localhost:8080`

## 📚 Documentation

- **Architecture**: See [`plans/movie-booking-platform-architecture.md`](plans/movie-booking-platform-architecture.md)
- **Implementation Guide**: See [`plans/implementation-guide.md`](plans/implementation-guide.md)
- **Requirements**: See [`domain_excercise_booking_platform.md`](domain_excercise_booking_platform.md)

## 📄 License

This is an interview assessment project.

---
