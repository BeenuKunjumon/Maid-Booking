package com.rupeek.maid_booking.booking;

import com.rupeek.maid_booking.domain.RecurringRule;
import java.time.LocalDate;

public record RecurringBookingRequest(String customerId, String maidId, LocalDate startDate, RecurringRule rule) {}
