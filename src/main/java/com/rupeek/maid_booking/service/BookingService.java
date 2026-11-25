package com.rupeek.maid_booking.service;

import com.rupeek.maid_booking.booking.*;
import com.rupeek.maid_booking.cancellation.CancellationPolicy;
import com.rupeek.maid_booking.domain.*;
import com.rupeek.maid_booking.exception.*;
import com.rupeek.maid_booking.payment.PaymentGateway;
import com.rupeek.maid_booking.pricing.PricingStrategy;
import com.rupeek.maid_booking.pricing.StandardPricingStrategy;
import com.rupeek.maid_booking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingService {
    private final MaidRepository maidRepository;
    private final BookingRepository bookingRepository;
    private final Map<BookingType, BookingStrategy> strategies;
    private final PaymentGateway paymentGateway;
    private final CancellationPolicy cancellationPolicy;
    private final PricingStrategy pricingStrategy;
    private final Map<String, String> paymentsByBooking = new ConcurrentHashMap<>();
    private final Map<String, String> paymentsByIdempotencyKey = new ConcurrentHashMap<>();
    private final Map<String, String> idempotencyRequests = new ConcurrentHashMap<>();

    public BookingService(MaidRepository maidRepository, BookingRepository bookingRepository,
                          List<BookingStrategy> strategyList, PaymentGateway paymentGateway,
                          CancellationPolicy cancellationPolicy) {
        this(maidRepository, bookingRepository, strategyList, paymentGateway, cancellationPolicy,
                new StandardPricingStrategy());
    }

    @Autowired
    public BookingService(MaidRepository maidRepository, BookingRepository bookingRepository,
                          List<BookingStrategy> strategyList, PaymentGateway paymentGateway,
                          CancellationPolicy cancellationPolicy, PricingStrategy pricingStrategy) {
        this.maidRepository = maidRepository;
        this.bookingRepository = bookingRepository;
        this.strategies = new EnumMap<>(BookingType.class);
        strategyList.forEach(s -> strategies.put(s.type(), s));
        this.paymentGateway = paymentGateway;
        this.cancellationPolicy = cancellationPolicy;
        this.pricingStrategy = pricingStrategy;
    }

    public Booking book(BookingType type, Object request) {
        ScheduledBookingRequest scheduled = request instanceof ScheduledBookingRequest s ? s : null;
        RecurringBookingRequest recurring = request instanceof RecurringBookingRequest r ? r : null;
        String customerId = scheduled != null ? scheduled.customerId() : recurring.customerId();
        String maidId = scheduled != null ? scheduled.maidId() : recurring.maidId();

        Maid maid = maidRepository.findById(maidId).orElseThrow(() -> new NotFoundException("Maid not found: " + maidId));
        BookingStrategy strategy = strategies.get(type);
        if (strategy == null) throw new IllegalArgumentException("Unsupported booking type: " + type);
        Set<TimeSlot> occurrences = strategy.resolveOccurrences(maid, request);

        synchronized (maidId.intern()) {
            for (TimeSlot occurrence : occurrences) {
                if (!maid.isAvailable(occurrence)) throw new ConflictException("Maid is outside availability");
                if (hasOverlap(maidId, occurrence)) throw new ConflictException("Maid already booked for overlapping slot");
            }
            BigDecimal amount = pricingStrategy.calculate(maid, occurrences);
            Booking booking = new Booking(UUID.randomUUID().toString(), customerId, maid, type, occurrences, amount);
            return bookingRepository.save(booking);
        }
    }

    private boolean hasOverlap(String maidId, TimeSlot requested) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getMaid().getId().equals(maidId))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .flatMap(b -> b.getOccurrences().stream())
                .anyMatch(requested::overlaps);
    }

    public String pay(String bookingId, String paymentMethodType) {
        return pay(bookingId, paymentMethodType, null);
    }

    public String pay(String bookingId, String paymentMethodType, String idempotencyKey) {
        String requestIdentity = bookingId + "|" + paymentMethodType.toUpperCase(Locale.ROOT);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            String existing = paymentsByIdempotencyKey.get(idempotencyKey);
            if (existing != null) {
                if (!requestIdentity.equals(idempotencyRequests.get(idempotencyKey))) {
                    throw new ConflictException("Idempotency key reused for a different payment request");
                }
                return existing;
            }
        }
        String paymentId = paymentsByBooking.computeIfAbsent(bookingId, id -> {
            Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found: " + id));
            if (booking.getStatus() != BookingStatus.CONFIRMED) throw new ConflictException("Booking cannot be paid");
            return paymentGateway.charge(id, booking.getAmount(), paymentMethodType);
        });
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            paymentsByIdempotencyKey.putIfAbsent(idempotencyKey, paymentId);
            idempotencyRequests.putIfAbsent(idempotencyKey, requestIdentity);
        }
        return paymentId;
    }

    public BigDecimal cancel(String bookingId, LocalDateTime occurrenceStart) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        if (booking.getStatus() == BookingStatus.CANCELLED) throw new ConflictException("Booking is already cancelled");

        synchronized (booking.getMaid().getId().intern()) {
            if (occurrenceStart == null) {
                BigDecimal refund = cancellationPolicy.refundableAmount(booking, null);
                booking.cancelWholeSeries();
                bookingRepository.save(booking);
                return refund;
            }
            TimeSlot occurrence = booking.getOccurrences().stream()
                    .filter(s -> s.start().equals(occurrenceStart))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Occurrence not found"));
            BigDecimal refund = cancellationPolicy.refundableAmount(booking, occurrenceStart);
            booking.cancelOccurrence(occurrence);
            bookingRepository.save(booking);
            return refund;
        }
    }
}
