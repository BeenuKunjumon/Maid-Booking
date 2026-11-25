package com.rupeek.maid_booking.search;

import com.rupeek.maid_booking.domain.*;
import java.math.BigDecimal;
import java.util.List;

public record SearchCriteria(String locality, ServiceType serviceType, Gender gender,
                             Double minimumRating, BigDecimal maximumPrice,
                             List<String> skills, TimeSlot requestedSlot) {}
