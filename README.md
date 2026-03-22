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

## 🛡️ Concurrency Handling (CRITICAL)

### Multi-Layered Approach

#### Layer 1: Optimistic Locking
```java
@Entity
public class Booking {
    @Version
    private Integer version;  // Auto-managed by JPA
}
```

**How it works**:
- JPA automatically increments version on each update
- Before update, checks if version matches database
- If mismatch → `OptimisticLockException` thrown
- Prevents lost updates in concurrent scenarios

#### Layer 2: Database Unique Constraint
```sql
ALTER TABLE booking_seats 
ADD CONSTRAINT uk_show_seat 
UNIQUE (show_id, seat_id);
```

**Why it's important**:
- Provides database-level protection
- Even if optimistic locking fails, DB rejects duplicate bookings
- Ensures data integrity at the lowest level

#### Layer 3: Application-Level Check
```java
List<Long> bookedSeatIds = bookingSeatRepository
    .findBookedSeatIds(showId, seatIds);

if (!bookedSeatIds.isEmpty()) {
    throw new SeatAlreadyBookedException(...);
}
```

**Benefits**:
- Early detection of conflicts
- Better user experience (fail fast)
- Reduces unnecessary database operations

### Why Optimistic Locking?

| Aspect | Optimistic | Pessimistic |
|--------|-----------|-------------|
| Performance | ✅ High | ❌ Lower |
| Scalability | ✅ Better | ❌ Limited |
| Deadlock Risk | ✅ None | ❌ Possible |
| Use Case Fit | ✅ Perfect for bookings | ❌ Overkill |

**Conclusion**: Optimistic locking is ideal because booking conflicts are relatively rare, and we prioritize performance and scalability.

## 📦 Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: Hibernate
- **Database**: PostgreSQL (H2 for testing)
- **Build Tool**: Maven
- **Lombok**: Code generation

## 📁 Project Structure

```
movie-booking-platform/
├── src/main/java/com/moviebooking/
│   ├── MovieBookingApplication.java
│   ├── controller/
│   │   ├── ShowController.java
│   │   └── BookingController.java
│   ├── service/
│   │   ├── ShowService.java
│   │   ├── BookingService.java
│   │   ├── PaymentService.java
│   │   └── discount/
│   │       ├── DiscountStrategy.java
│   │       ├── DiscountService.java
│   │       ├── ThirdTicketDiscountStrategy.java
│   │       └── AfternoonShowDiscountStrategy.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── MovieRepository.java
│   │   ├── TheatreRepository.java
│   │   ├── ScreenRepository.java
│   │   ├── ShowRepository.java
│   │   ├── SeatRepository.java
│   │   ├── BookingRepository.java
│   │   └── BookingSeatRepository.java
│   ├── entity/
│   │   ├── BaseEntity.java
│   │   ├── User.java
│   │   ├── Movie.java
│   │   ├── Theatre.java
│   │   ├── Screen.java
│   │   ├── Seat.java
│   │   ├── Show.java
│   │   ├── Booking.java
│   │   └── BookingSeat.java
│   ├── dto/
│   │   ├── request/
│   │   │   └── BookingRequest.java
│   │   └── response/
│   │       ├── ShowResponse.java
│   │       ├── TheatreShowResponse.java
│   │       ├── BookingResponse.java
│   │       └── ErrorResponse.java
│   ├── exception/
│   │   ├── SeatAlreadyBookedException.java
│   │   ├── InvalidShowException.java
│   │   ├── PaymentFailedException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   └── enums/
│       ├── BookingStatus.java
│       ├── SeatType.java
│       ├── PaymentMethod.java
│       └── ShowStatus.java
├── src/main/resources/
│   ├── application.properties
│   └── db/
│       ├── schema.sql
│       └── data.sql
└── src/test/java/com/moviebooking/
    └── service/
        └── BookingServiceTest.java
```

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

### Key Tables

#### bookings
- `id`: Primary key
- `user_id`: Foreign key to users
- `show_id`: Foreign key to shows
- `status`: PENDING, CONFIRMED, CANCELLED, FAILED
- `total_amount`: Final amount after discounts
- `discount_amount`: Total discount applied
- `payment_method`: CARD, UPI, NET_BANKING, WALLET
- **`version`**: Optimistic locking version (CRITICAL)

#### booking_seats
- `id`: Primary key
- `booking_id`: Foreign key to bookings
- `seat_id`: Foreign key to seats
- `show_id`: Foreign key to shows
- `price`: Seat price at booking time
- `status`: Booking status
- **UNIQUE CONSTRAINT**: `(show_id, seat_id)` - Prevents double booking

### Indexes

```sql
-- Performance indexes
CREATE INDEX idx_show_movie_screen ON shows(movie_id, screen_id);
CREATE INDEX idx_show_start_time ON shows(start_time);
CREATE INDEX idx_theatre_city ON theatres(city);
CREATE INDEX idx_booking_seat_show_seat ON booking_seats(show_id, seat_id);
CREATE INDEX idx_seat_screen ON seats(screen_id);
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

## 🧪 Testing

### Unit Tests

**BookingServiceTest** includes:
- Successful booking scenario
- Concurrent booking prevention
- Third ticket discount application
- Afternoon show discount application
- Payment failure handling

**Run Tests**:
```bash
mvn test
```

## 🚀 Running the Application

### Prerequisites
- Java 17+
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

## 📊 Performance Considerations

### Query Optimization
- **JOIN FETCH**: Prevents N+1 query problems
- **Indexes**: Strategic indexes on frequently queried columns
- **Projections**: Fetch only required fields

### Connection Pooling
- **HikariCP**: Default in Spring Boot
- **Max Pool Size**: 20 connections
- **Min Idle**: 5 connections

### Transaction Management
- **Isolation Level**: READ_COMMITTED
- **Prevents**: Dirty reads
- **Allows**: Concurrent reads

## 🔒 Security

### Input Validation
- `@Valid` annotation on request bodies
- Bean Validation constraints
- Custom validation logic

### SQL Injection Prevention
- JPA/JPQL with parameterized queries
- No string concatenation in queries

### Future Enhancements
- JWT authentication
- Role-based access control
- Rate limiting
- API key management

## 📈 Scalability

### Current Design Supports
- Horizontal scaling (stateless services)
- Database read replicas
- Connection pooling

### Future Enhancements
- Redis caching for show listings
- Kafka for async event processing
- CDN for static content
- Multi-region deployment

## 🎓 Interview Talking Points

### 1. Concurrency Handling
**Q**: Why optimistic locking over pessimistic?

**A**: Optimistic locking is better for our use case because:
- Booking conflicts are relatively rare
- Better performance (no locks held)
- Better scalability (no lock contention)
- No deadlock risk

### 2. Scalability
**Q**: How would you scale to handle 10,000 concurrent bookings?

**A**:
- Horizontal scaling with load balancer
- Database read replicas for browse APIs
- Redis for caching and distributed locking
- Kafka for async processing
- Database sharding by city/region

### 3. Failure Handling
**Q**: What happens if payment service is down?

**A**:
- Circuit breaker pattern to fail fast
- Retry mechanism with exponential backoff
- Queue-based processing for delayed confirmation
- User notification about status

### 4. Data Consistency
**Q**: How do you ensure consistency across booking and payment?

**A**:
- Database transactions with proper isolation
- Idempotent API design
- Saga pattern for distributed transactions (future)
- Compensating transactions for rollback

## 📝 Code Quality

### SOLID Principles
- **S**: Single Responsibility - Each class has one purpose
- **O**: Open/Closed - Strategy pattern allows extension
- **L**: Liskov Substitution - Proper inheritance
- **I**: Interface Segregation - Focused interfaces
- **D**: Dependency Inversion - Dependency injection

### Best Practices
- Meaningful names
- Comprehensive comments
- Exception handling
- Logging
- Transaction management

## 📚 Documentation

- **Architecture**: See [`plans/movie-booking-platform-architecture.md`](plans/movie-booking-platform-architecture.md)
- **Implementation Guide**: See [`plans/implementation-guide.md`](plans/implementation-guide.md)
- **Requirements**: See [`domain_excercise_booking_platform.md`](domain_excercise_booking_platform.md)

## 👨‍💻 Author

Senior Backend Engineer with 6+ years of experience in Java, Spring Boot, and distributed systems.

## 📄 License

This is an interview assessment project.

---

**Note**: This is a production-quality implementation demonstrating real-world design patterns, concurrency handling, and best practices suitable for a senior backend engineer role.
