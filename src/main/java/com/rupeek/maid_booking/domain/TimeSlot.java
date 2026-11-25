package com.rupeek.maid_booking.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record TimeSlot(LocalDateTime start, LocalDateTime end) {
    public TimeSlot {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (!end.isAfter(start)) throw new IllegalArgumentException("Slot end must be after start");
    }

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    public boolean isWithin(TimeSlot availability) {
        return !start.isBefore(availability.start()) && !end.isAfter(availability.end());
    }
}
