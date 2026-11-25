package com.rupeek.maid_booking.api;

import com.rupeek.maid_booking.booking.*;
import com.rupeek.maid_booking.domain.*;
import com.rupeek.maid_booking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    public record ScheduledRequest(@NotBlank String customerId, @NotBlank String maidId, TimeSlot slot) {}
    public record RecurringRequest(@NotBlank String customerId, @NotBlank String maidId,
                                   LocalDate startDate, Set<DayOfWeek> days,
                                   LocalTime startTime, int durationMinutes, LocalDate endDate) {}
    public record PayRequest(@NotBlank String method) {}

    @PostMapping("/instant")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking instant(@Valid @RequestBody ScheduledRequest request) {
        return bookingService.book(BookingType.INSTANT, new ScheduledBookingRequest(request.customerId(), request.maidId(), request.slot()));
    }

    @PostMapping("/scheduled")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking scheduled(@Valid @RequestBody ScheduledRequest request) {
        return bookingService.book(BookingType.SCHEDULED, new ScheduledBookingRequest(request.customerId(), request.maidId(), request.slot()));
    }

    @PostMapping("/recurring")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking recurring(@Valid @RequestBody RecurringRequest request) {
        RecurringRule rule = new RecurringRule(request.days(), request.startTime(), request.durationMinutes(), request.endDate());
        return bookingService.book(BookingType.RECURRING,
                new RecurringBookingRequest(request.customerId(), request.maidId(), request.startDate(), rule));
    }

    @PostMapping("/{id}/payments")
    public Map<String, Object> pay(@PathVariable String id, @Valid @RequestBody PayRequest request,
                                   @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        String paymentId = bookingService.pay(id, request.method().toUpperCase(), idempotencyKey);
        return Map.of("paymentId", paymentId);
    }

    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable String id,
                                      @RequestParam(required=false) LocalDateTime occurrenceStart) {
        BigDecimal refund = bookingService.cancel(id, occurrenceStart);
        return Map.of("refundAmount", refund);
    }
}
