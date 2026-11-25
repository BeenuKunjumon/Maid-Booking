package com.rupeek.maid_booking;

import com.rupeek.maid_booking.booking.*;
import com.rupeek.maid_booking.cancellation.SimpleCancellationPolicy;
import com.rupeek.maid_booking.domain.*;
import com.rupeek.maid_booking.exception.ConflictException;
import com.rupeek.maid_booking.infrastructure.*;
import com.rupeek.maid_booking.payment.MockPaymentGateway;
import com.rupeek.maid_booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTests {
    private InMemoryMaidRepository maidRepository;
    private InMemoryBookingRepository bookingRepository;
    private BookingService service;
    private Maid maid;

    @BeforeEach
    void setUp() {
        maidRepository = new InMemoryMaidRepository();
        bookingRepository = new InMemoryBookingRepository();
        service = new BookingService(maidRepository, bookingRepository,
                List.of(new InstantBookingStrategy(), new ScheduledBookingStrategy(), new RecurringBookingStrategy()),
                new MockPaymentGateway(), new SimpleCancellationPolicy());

        TimeSlot availability = new TimeSlot(LocalDateTime.of(2026, 12, 1, 8, 0),
                LocalDateTime.of(2026, 12, 1, 18, 0));
        maid = new Maid("maid-1", "Asha", "Indiranagar", Gender.FEMALE,
                Set.of(ServiceType.CLEANING), Set.of("dusting"), BigDecimal.valueOf(300), 4.8,
                Set.of(new AvailabilityWindow(availability)));
        maidRepository.save(maid);
    }

    @Test
    void rejectsOverlappingScheduledBooking() {
        TimeSlot first = new TimeSlot(LocalDateTime.of(2026, 12, 1, 9, 0),
                LocalDateTime.of(2026, 12, 1, 11, 0));
        service.book(BookingType.SCHEDULED,
                new ScheduledBookingRequest("customer-1", "maid-1", first));

        TimeSlot overlapping = new TimeSlot(LocalDateTime.of(2026, 12, 1, 10, 0),
                LocalDateTime.of(2026, 12, 1, 12, 0));
        assertThrows(ConflictException.class, () -> service.book(BookingType.SCHEDULED,
                new ScheduledBookingRequest("customer-2", "maid-1", overlapping)));
    }

    @Test
    void cancelsOccurrenceAndFreesSlot() {
        TimeSlot first = new TimeSlot(LocalDateTime.of(2026, 12, 1, 9, 0),
                LocalDateTime.of(2026, 12, 1, 11, 0));
        Booking booking = service.book(BookingType.SCHEDULED,
                new ScheduledBookingRequest("customer-1", "maid-1", first));

        service.cancel(booking.getId(), first.start());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        TimeSlot replacement = new TimeSlot(LocalDateTime.of(2026, 12, 1, 9, 30),
                LocalDateTime.of(2026, 12, 1, 10, 30));
        assertDoesNotThrow(() -> service.book(BookingType.SCHEDULED,
                new ScheduledBookingRequest("customer-2", "maid-1", replacement)));
    }

    @Test
    void paymentIsIdempotent() {
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 12, 1, 12, 0),
                LocalDateTime.of(2026, 12, 1, 13, 0));
        Booking booking = service.book(BookingType.INSTANT,
                new ScheduledBookingRequest("customer-1", "maid-1", slot));

        String first = service.pay(booking.getId(), "UPI");
        String second = service.pay(booking.getId(), "UPI");

        assertEquals(first, second);
    }
}
