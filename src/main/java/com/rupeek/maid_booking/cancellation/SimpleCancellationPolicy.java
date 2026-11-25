package com.rupeek.maid_booking.cancellation;

import com.rupeek.maid_booking.domain.Booking;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class SimpleCancellationPolicy implements CancellationPolicy {
    @Override public BigDecimal refundableAmount(Booking booking, LocalDateTime occurrenceStart) {
        if (occurrenceStart == null) return booking.getAmount();
        long hours = java.time.Duration.between(LocalDateTime.now(), occurrenceStart).toHours();
        if (hours >= 24) return booking.getAmount();
        if (hours >= 6) return booking.getAmount().multiply(BigDecimal.valueOf(0.50));
        return BigDecimal.ZERO;
    }
}
