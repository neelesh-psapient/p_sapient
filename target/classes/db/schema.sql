-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Movies table
CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    language VARCHAR(50) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Theatres table
CREATE TABLE IF NOT EXISTS theatres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_theatre_city ON theatres(city);

-- Screens table
CREATE TABLE IF NOT EXISTS screens (
    id BIGSERIAL PRIMARY KEY,
    theatre_id BIGINT NOT NULL REFERENCES theatres(id),
    name VARCHAR(100) NOT NULL,
    total_seats INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Seats table
CREATE TABLE IF NOT EXISTS seats (
    id BIGSERIAL PRIMARY KEY,
    screen_id BIGINT NOT NULL REFERENCES screens(id),
    seat_number VARCHAR(10) NOT NULL,
    seat_type VARCHAR(20) NOT NULL CHECK (seat_type IN ('REGULAR', 'PREMIUM', 'VIP')),
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(screen_id, seat_number)
);

CREATE INDEX IF NOT EXISTS idx_seat_screen ON seats(screen_id);

-- Shows table
CREATE TABLE IF NOT EXISTS shows (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id),
    screen_id BIGINT NOT NULL REFERENCES screens(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_show_movie_screen ON shows(movie_id, screen_id);
CREATE INDEX IF NOT EXISTS idx_show_start_time ON shows(start_time);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    show_id BIGINT NOT NULL REFERENCES shows(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED')),
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2),
    payment_method VARCHAR(20),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Booking seats table (junction table with concurrency control)
CREATE TABLE IF NOT EXISTS booking_seats (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    seat_id BIGINT NOT NULL REFERENCES seats(id),
    show_id BIGINT NOT NULL REFERENCES shows(id),
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(show_id, seat_id)
);

CREATE INDEX IF NOT EXISTS idx_booking_seat_show_seat ON booking_seats(show_id, seat_id);
