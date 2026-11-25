package com.rupeek.maid_booking.repository;

import com.rupeek.maid_booking.domain.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findAll();
}
