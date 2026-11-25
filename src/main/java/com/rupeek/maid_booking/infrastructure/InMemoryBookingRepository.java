package com.rupeek.maid_booking.infrastructure;

import com.rupeek.maid_booking.domain.Booking;
import com.rupeek.maid_booking.repository.BookingRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override public Booking save(Booking booking) { bookings.put(booking.getId(), booking); return booking; }
    @Override public Optional<Booking> findById(String id) { return Optional.ofNullable(bookings.get(id)); }
    @Override public List<Booking> findAll() { return new ArrayList<>(bookings.values()); }
}
