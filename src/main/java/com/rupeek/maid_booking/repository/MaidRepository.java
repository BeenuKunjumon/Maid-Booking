package com.rupeek.maid_booking.repository;

import com.rupeek.maid_booking.domain.Maid;
import java.util.List;
import java.util.Optional;

public interface MaidRepository {
    Maid save(Maid maid);
    Optional<Maid> findById(String id);
    List<Maid> findAll();
}
