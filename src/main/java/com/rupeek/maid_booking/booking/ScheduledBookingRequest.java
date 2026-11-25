package com.rupeek.maid_booking.booking;

import com.rupeek.maid_booking.domain.TimeSlot;

public record ScheduledBookingRequest(String customerId, String maidId, TimeSlot slot) {}
