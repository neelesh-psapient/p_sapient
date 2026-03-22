package com.moviebooking.service;

import com.moviebooking.dto.request.BookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.entity.*;
import com.moviebooking.enums.BookingStatus;
import com.moviebooking.exception.*;
import com.moviebooking.repository.*;
import com.moviebooking.service.discount.BookingContext;
import com.moviebooking.service.discount.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for booking-related operations
 * 
 * CRITICAL: This service implements the core booking logic with
 * optimistic locking for concurrency control
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final DiscountService discountService;
    
    /**
     * Create a new booking with optimistic locking for concurrency control
     * 
     * Transaction Flow:
     * 1. Validate user and show
     * 2. Check seat availability
     * 3. Create booking with PENDING status
     * 4. Lock seats by creating booking_seat records
     * 5. Calculate discount
     * 6. Process payment (mock)
     * 7. Update booking to CONFIRMED
     * 8. Commit transaction
     * 
     * Concurrency Handling:
     * - Optimistic locking on Booking entity (@Version)
     * - Unique constraint on (show_id, seat_id) in booking_seats
     * - If conflict occurs, OptimisticLockException is thrown
     * 
     * @param request Booking request
     * @return Booking response
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for user: {}, show: {}, seats: {}", 
            request.getUserId(), request.getShowId(), request.getSeats());
        
        // Step 1: Validate user
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
        
        // Step 2: Validate show and fetch with relationships
        Show show = showRepository.findByIdWithRelationships(request.getShowId())
            .orElseThrow(() -> new InvalidShowException("Show not found with ID: " + request.getShowId()));
        
        // Step 3: Validate and fetch seats
        List<Seat> seats = validateAndFetchSeats(show, request.getSeats());
        
        // Step 4: Check if seats are already booked (concurrency check)
        List<Long> seatIds = seats.stream()
            .map(Seat::getId)
            .collect(Collectors.toList());
        
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIds(show.getId(), seatIds);
        
        if (!bookedSeatIds.isEmpty()) {
            List<String> bookedSeatNumbers = seats.stream()
                .filter(s -> bookedSeatIds.contains(s.getId()))
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());
            
            throw new SeatAlreadyBookedException(
                "Seats already booked: " + bookedSeatNumbers, bookedSeatNumbers);
        }
        
        // Step 5: Calculate total amount
        BigDecimal totalAmount = seats.stream()
            .map(Seat::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Step 6: Create booking with PENDING status
        Booking booking = Booking.builder()
            .user(user)
            .show(show)
            .status(BookingStatus.PENDING)
            .totalAmount(totalAmount)
            .paymentMethod(request.getPaymentMethod())
            .build();
        
        // Step 7: Add booking seats (this locks the seats)
        for (Seat seat : seats) {
            BookingSeat bookingSeat = BookingSeat.builder()
                .seat(seat)
                .show(show)
                .price(seat.getPrice())
                .status(BookingStatus.PENDING)
                .build();
            booking.addBookingSeat(bookingSeat);
        }
        
        // Step 8: Save booking (this will throw OptimisticLockException if conflict)
        try {
            booking = bookingRepository.save(booking);
            log.info("Booking created with PENDING status: {}", booking.getId());
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation - seats already booked", e);
            throw new SeatAlreadyBookedException(
                "Seats are no longer available", request.getSeats());
        }
        
        // Step 9: Calculate discount
        BookingContext context = BookingContext.builder()
            .booking(booking)
            .show(show)
            .seatCount(seats.size())
            .build();
        
        BigDecimal discount = discountService.calculateTotalDiscount(context);
        booking.setDiscountAmount(discount);
        
        BigDecimal finalAmount = totalAmount.subtract(discount);
        booking.setTotalAmount(finalAmount);
        
        // Step 10: Process payment (mock implementation)
        try {
            paymentService.processPayment(booking, finalAmount);
            log.info("Payment processed successfully for booking: {}", booking.getId());
        } catch (PaymentFailedException e) {
            log.error("Payment failed for booking: {}", booking.getId(), e);
            booking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(booking);
            throw e;
        }
        
        // Step 11: Confirm booking
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.getBookingSeats().forEach(bs -> bs.setStatus(BookingStatus.CONFIRMED));
        booking = bookingRepository.save(booking);
        
        log.info("Booking confirmed successfully: {}", booking.getId());
        
        // Step 12: Build response
        return BookingResponse.builder()
            .bookingId(booking.getId().toString())
            .status(booking.getStatus().name())
            .amount(booking.getTotalAmount())
            .discount(discount)
            .seats(request.getSeats())
            .build();
    }
    
    /**
     * Validate seat numbers and fetch seat entities
     */
    private List<Seat> validateAndFetchSeats(Show show, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByScreenAndSeatNumbers(
            show.getScreen().getId(), seatNumbers);
        
        if (seats.size() != seatNumbers.size()) {
            List<String> foundSeatNumbers = seats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());
            
            List<String> invalidSeats = seatNumbers.stream()
                .filter(sn -> !foundSeatNumbers.contains(sn))
                .collect(Collectors.toList());
            
            throw new InvalidSeatException("Invalid seats: " + invalidSeats);
        }
        
        return seats;
    }
    
    /**
     * Get user bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private BookingResponse mapToResponse(Booking booking) {
        List<String> seatNumbers = booking.getBookingSeats().stream()
            .map(bs -> bs.getSeat().getSeatNumber())
            .collect(Collectors.toList());
        
        return BookingResponse.builder()
            .bookingId(booking.getId().toString())
            .status(booking.getStatus().name())
            .amount(booking.getTotalAmount())
            .discount(booking.getDiscountAmount())
            .seats(seatNumbers)
            .build();
    }
}
