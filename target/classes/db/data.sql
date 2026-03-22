-- Insert users
INSERT INTO users (name, email) VALUES
('John Doe', 'john@example.com'),
('Jane Smith', 'jane@example.com'),
('Bob Johnson', 'bob@example.com')
ON CONFLICT (email) DO NOTHING;

-- Insert movies
INSERT INTO movies (name, language, genre) VALUES
('Inception', 'English', 'Sci-Fi'),
('The Dark Knight', 'English', 'Action'),
('Interstellar', 'English', 'Sci-Fi'),
('Dangal', 'Hindi', 'Drama'),
('3 Idiots', 'Hindi', 'Comedy')
ON CONFLICT DO NOTHING;

-- Insert theatres
INSERT INTO theatres (name, city, address) VALUES
('PVR Cinemas', 'Mumbai', 'Phoenix Mall, Lower Parel'),
('INOX', 'Mumbai', 'R City Mall, Ghatkopar'),
('Cinepolis', 'Delhi', 'DLF Place, Saket'),
('PVR Cinemas', 'Delhi', 'Select City Walk, Saket'),
('INOX', 'Bangalore', 'Garuda Mall, Magrath Road')
ON CONFLICT DO NOTHING;

-- Insert screens
INSERT INTO screens (theatre_id, name, total_seats) VALUES
(1, 'Screen 1', 100),
(1, 'Screen 2', 150),
(2, 'Screen 1', 120),
(3, 'Screen 1', 100),
(4, 'Screen 1', 130)
ON CONFLICT DO NOTHING;

-- Insert seats for Screen 1 (100 seats: 10 rows x 10 columns)
DO $$
DECLARE
    row_num INTEGER;
    col_num INTEGER;
    seat_num VARCHAR(10);
    seat_price DECIMAL(10, 2);
    seat_category VARCHAR(20);
BEGIN
    FOR row_num IN 1..10 LOOP
        FOR col_num IN 1..10 LOOP
            seat_num := CHR(64 + row_num) || col_num;
            
            -- First 2 rows are PREMIUM
            IF row_num <= 2 THEN
                seat_category := 'PREMIUM';
                seat_price := 300.00;
            ELSE
                seat_category := 'REGULAR';
                seat_price := 200.00;
            END IF;
            
            INSERT INTO seats (screen_id, seat_number, seat_type, price)
            VALUES (1, seat_num, seat_category, seat_price)
            ON CONFLICT (screen_id, seat_number) DO NOTHING;
        END LOOP;
    END LOOP;
END $$;

-- Insert shows for today and tomorrow
INSERT INTO shows (movie_id, screen_id, start_time, end_time, status) VALUES
-- Today's shows
(1, 1, CURRENT_DATE + INTERVAL '10 hours', CURRENT_DATE + INTERVAL '13 hours', 'ACTIVE'),
(1, 1, CURRENT_DATE + INTERVAL '14 hours', CURRENT_DATE + INTERVAL '17 hours', 'ACTIVE'),
(1, 1, CURRENT_DATE + INTERVAL '19 hours', CURRENT_DATE + INTERVAL '22 hours', 'ACTIVE'),
(2, 2, CURRENT_DATE + INTERVAL '11 hours', CURRENT_DATE + INTERVAL '14 hours', 'ACTIVE'),
(3, 3, CURRENT_DATE + INTERVAL '13 hours', CURRENT_DATE + INTERVAL '16 hours', 'ACTIVE'),
-- Tomorrow's shows
(1, 1, CURRENT_DATE + INTERVAL '1 day' + INTERVAL '10 hours', CURRENT_DATE + INTERVAL '1 day' + INTERVAL '13 hours', 'ACTIVE'),
(2, 1, CURRENT_DATE + INTERVAL '1 day' + INTERVAL '14 hours', CURRENT_DATE + INTERVAL '1 day' + INTERVAL '17 hours', 'ACTIVE')
ON CONFLICT DO NOTHING;
