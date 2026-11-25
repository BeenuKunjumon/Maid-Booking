package com.rupeek.maid_booking.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record RecurringRule(Set<DayOfWeek> days, LocalTime startTime, int durationMinutes,
                            LocalDate endDate) {
    public RecurringRule {
        if (days == null || days.isEmpty()) throw new IllegalArgumentException("Recurring days are required");
        if (startTime == null || endDate == null) throw new IllegalArgumentException("Recurring time and end date are required");
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration must be positive");
    }
}
