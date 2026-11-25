package com.rupeek.maid_booking.infrastructure;

import com.rupeek.maid_booking.domain.Maid;
import com.rupeek.maid_booking.repository.MaidRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMaidRepository implements MaidRepository {
    private final Map<String, Maid> maids = new ConcurrentHashMap<>();

    @Override public Maid save(Maid maid) { maids.put(maid.getId(), maid); return maid; }
    @Override public Optional<Maid> findById(String id) { return Optional.ofNullable(maids.get(id)); }
    @Override public List<Maid> findAll() { return new ArrayList<>(maids.values()); }
}
