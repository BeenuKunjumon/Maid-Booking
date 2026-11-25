package com.rupeek.maid_booking.cancellation;

import com.rupeek.maid_booking.domain.Booking;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CancellationPolicy {
    BigDecimal refundableAmount(Booking booking, LocalDateTime occurrenceStart);
}
