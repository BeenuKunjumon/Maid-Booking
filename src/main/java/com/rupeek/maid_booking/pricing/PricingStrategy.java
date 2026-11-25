package com.rupeek.maid_booking.pricing;

import com.rupeek.maid_booking.domain.Maid;
import com.rupeek.maid_booking.domain.TimeSlot;

import java.math.BigDecimal;
import java.util.Set;

public interface PricingStrategy {
    BigDecimal calculate(Maid maid, Set<TimeSlot> occurrences);
}
