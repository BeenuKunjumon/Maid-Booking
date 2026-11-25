package com.rupeek.maid_booking.booking;

import com.rupeek.maid_booking.domain.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class RecurringBookingStrategy implements BookingStrategy {
    @Override public BookingType type() { return BookingType.RECURRING; }
    @Override public Set<TimeSlot> resolveOccurrences(Maid maid, Object request) {
        if (!(request instanceof RecurringBookingRequest r)) throw new IllegalArgumentException("Recurring rule is required");
        RecurringRule rule = r.rule();
        Set<TimeSlot> result = new LinkedHashSet<>();
        for (LocalDate date = r.startDate(); !date.isAfter(rule.endDate()); date = date.plusDays(1)) {
            if (rule.days().contains(date.getDayOfWeek())) {
                LocalDateTime start = LocalDateTime.of(date, rule.startTime());
                result.add(new TimeSlot(start, start.plusMinutes(rule.durationMinutes())));
            }
        }
        return result;
    }
}
