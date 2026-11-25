package com.rupeek.maid_booking.booking;

import com.rupeek.maid_booking.domain.*;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class InstantBookingStrategy implements BookingStrategy {
    @Override public BookingType type() { return BookingType.INSTANT; }
    @Override public Set<TimeSlot> resolveOccurrences(Maid maid, Object request) {
        if (!(request instanceof ScheduledBookingRequest r)) throw new IllegalArgumentException("Slot is required");
        return Set.of(r.slot());
    }
}
