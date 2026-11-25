package com.rupeek.maid_booking.pricing;

import com.rupeek.maid_booking.domain.Maid;
import com.rupeek.maid_booking.domain.TimeSlot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Set;

@Component
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculate(Maid maid, Set<TimeSlot> occurrences) {
        long minutes = occurrences.stream()
                .mapToLong(slot -> Duration.between(slot.start(), slot.end()).toMinutes())
                .sum();
        return maid.getHourlyRate()
                .multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
