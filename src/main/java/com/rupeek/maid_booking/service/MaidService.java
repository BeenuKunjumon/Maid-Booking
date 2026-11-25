package com.rupeek.maid_booking.service;

import com.rupeek.maid_booking.domain.Maid;
import com.rupeek.maid_booking.repository.MaidRepository;
import org.springframework.stereotype.Service;

@Service
public class MaidService {
    private final MaidRepository repository;

    public MaidService(MaidRepository repository) { this.repository = repository; }

    public Maid add(Maid maid) { return repository.save(maid); }
}
