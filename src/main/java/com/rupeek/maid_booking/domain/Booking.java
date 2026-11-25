package com.rupeek.maid_booking.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Booking {
    private final String id;
    private final String customerId;
    private final Maid maid;
    private final BookingType type;
    private final Set<TimeSlot> occurrences;
    private BookingStatus status;
    private final BigDecimal amount;

    public Booking(String id, String customerId, Maid maid, BookingType type,
                   Set<TimeSlot> occurrences, BigDecimal amount) {
        if (id == null || customerId == null || maid == null || type == null) throw new IllegalArgumentException("Booking fields are required");
        if (occurrences == null || occurrences.isEmpty()) throw new IllegalArgumentException("At least one occurrence is required");
        if (amount == null || amount.signum() < 0) throw new IllegalArgumentException("Valid amount is required");
        this.id = id;
        this.customerId = customerId;
        this.maid = maid;
        this.type = type;
        this.occurrences = new HashSet<>(occurrences);
        this.status = BookingStatus.CONFIRMED;
        this.amount = amount;
    }

    public void cancelOccurrence(TimeSlot occurrence) {
        if (!occurrences.contains(occurrence)) throw new IllegalArgumentException("Occurrence is not part of this booking");
        occurrences.remove(occurrence);
        if (occurrences.isEmpty()) status = BookingStatus.CANCELLED;
    }

    public void cancelWholeSeries() { status = BookingStatus.CANCELLED; }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Maid getMaid() { return maid; }
    public BookingType getType() { return type; }
    public Set<TimeSlot> getOccurrences() { return Set.copyOf(occurrences); }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
}
