package com.rupeek.maid_booking.booking;

import com.rupeek.maid_booking.domain.*;
import java.util.Set;

public interface BookingStrategy {
    BookingType type();
    Set<TimeSlot> resolveOccurrences(Maid maid, Object request);
}
