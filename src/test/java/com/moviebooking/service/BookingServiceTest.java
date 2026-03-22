package com.moviebooking.service;

import com.moviebooking.dto.request.BookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.entity.*;
import com.moviebooking.enums.BookingStatus;
import com.moviebooking.enums.PaymentMethod;
import com.moviebooking.enums.SeatType;
import com.moviebooking.enums.ShowStatus;
import com.moviebooking.exception.SeatAlreadyBookedException;
import com.moviebooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceTest {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private TheatreRepository theatreRepository;
    
    @Autowired
    private ScreenRepository screenRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ShowRepository showRepository;
    
    private User testUser;
    private Show testShow;
    private List<Seat> testSeats;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
            .name("Test User")
            .email("test" + System.currentTimeMillis() + "@example.com")
            .build();
        testUser = userRepository.save(testUser);
        
        // Create test movie
        Movie movie = Movie.builder()
            .name("Test Movie")
            .language("English")
            .genre("Action")
            .build();
        movie = movieRepository.save(movie);
        
        // Create test theatre
        Theatre theatre = Theatre.builder()
            .name("Test Theatre")
            .city("Mumbai")
            .address("Test Address")
            .build();
        theatre = theatreRepository.save(theatre);
        
        // Create test screen
        Screen screen = Screen.builder()
            .theatre(theatre)
            .name("Screen 1")
            .totalSeats(10)
            .build();
        screen = screenRepository.save(screen);
        
        // Create test seats
        testSeats = Arrays.asList(
            createSeat(screen, "A1", SeatType.REGULAR, new BigDecimal("200")),
            createSeat(screen, "A2", SeatType.REGULAR, new BigDecimal("200")),
            createSeat(screen, "A3", SeatType.REGULAR, new BigDecimal("200"))
        );
        
        // Create test show
        testShow = Show.builder()
            .movie(movie)
            .screen(screen)
            .startTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0))
            .endTime(LocalDateTime.now().plusDays(1).withHour(17).withMinute(0))
            .status(ShowStatus.ACTIVE)
            .build();
        testShow = showRepository.save(testShow);
    }
    
    private Seat createSeat(Screen screen, String seatNumber, SeatType seatType, BigDecimal price) {
        Seat seat = Seat.builder()
            .screen(screen)
            .seatNumber(seatNumber)
            .seatType(seatType)
            .price(price)
            .build();
        return seatRepository.save(seat);
    }
    
    @Test
    void testCreateBooking_Success() {
        // Given
        BookingRequest request = BookingRequest.builder()
            .userId(testUser.getId())
            .showId(testShow.getId())
            .seats(Arrays.asList("A1", "A2"))
            .paymentMethod(PaymentMethod.CARD)
            .build();
        
        // When
        BookingResponse response = bookingService.createBooking(request);
        
        // Then
        assertNotNull(response.getBookingId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(2, response.getSeats().size());
        assertTrue(response.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    void testCreateBooking_WithThirdTicketDiscount() {
        // Given
        BookingRequest request = BookingRequest.builder()
            .userId(testUser.getId())
            .showId(testShow.getId())
            .seats(Arrays.asList("A1", "A2", "A3"))
            .paymentMethod(PaymentMethod.CARD)
            .build();
        
        // When
        BookingResponse response = bookingService.createBooking(request);
        
        // Then
        assertNotNull(response.getDiscount());
        assertTrue(response.getDiscount().compareTo(BigDecimal.ZERO) > 0);
        // 50% discount on 3rd ticket (200 * 0.5 = 100)
        assertEquals(new BigDecimal("100.00"), response.getDiscount());
    }
    
    @Test
    void testCreateBooking_SeatAlreadyBooked() {
        // Given
        BookingRequest request1 = BookingRequest.builder()
            .userId(testUser.getId())
            .showId(testShow.getId())
            .seats(Arrays.asList("A1", "A2"))
            .paymentMethod(PaymentMethod.CARD)
            .build();
        
        // First booking
        bookingService.createBooking(request1);
        
        // Try to book same seats again
        BookingRequest request2 = BookingRequest.builder()
            .userId(testUser.getId())
            .showId(testShow.getId())
            .seats(Arrays.asList("A1", "A2"))
            .paymentMethod(PaymentMethod.CARD)
            .build();
        
        // When & Then
        assertThrows(SeatAlreadyBookedException.class, () -> {
            bookingService.createBooking(request2);
        });
    }
    
    @Test
    void testConcurrentBooking_ShouldPreventDoubleBooking() throws Exception {
        // Given
        User user2 = User.builder()
            .name("Test User 2")
            .email("test2" + System.currentTimeMillis() + "@example.com")
            .build();
        user2 = userRepository.save(user2);
        
        BookingRequest request1 = BookingRequest.builder()
            .userId(testUser.getId())
            .showId(testShow.getId())
            .seats(Arrays.asList("A1", "A2"))
            .paymentMethod(PaymentMethod.CARD)
            .build();
        
        BookingRequest request2 = BookingRequest.builder()
            .userId(user2.getId())
            .showId(testShow.getId())
            .seats(Arrays.asList("A1", "A2"))
            .paymentMethod(PaymentMethod.CARD)
            .build();
        
        // When - Execute concurrent bookings
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        executor.submit(() -> {
            try {
                bookingService.createBooking(request1);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });
        
        executor.submit(() -> {
            try {
                bookingService.createBooking(request2);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Then - Only one booking should succeed
        assertEquals(1, successCount.get(), "Only one booking should succeed");
        assertEquals(1, failureCount.get(), "One booking should fail");
    }
}
