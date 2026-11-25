package com.rupeek.maid_booking.pricing;

import com.rupeek.maid_booking.domain.Maid;
import com.rupeek.maid_booking.domain.TimeSlot;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Set;

@Component
@Primary
public class WeekendDynamicPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculate(Maid maid, Set<TimeSlot> occurrences) {
        BigDecimal total = BigDecimal.ZERO;
        for (TimeSlot slot : occurrences) {
            long minutes = Duration.between(slot.start(), slot.end()).toMinutes();
            BigDecimal amount = maid.getHourlyRate()
                    .multiply(BigDecimal.valueOf(minutes))
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            DayOfWeek day = slot.start().getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                amount = amount.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(0.20)));
            }
            total = total.add(amount);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
