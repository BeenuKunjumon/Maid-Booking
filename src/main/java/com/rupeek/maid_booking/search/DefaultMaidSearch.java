package com.rupeek.maid_booking.search;

import com.rupeek.maid_booking.domain.Maid;
import com.rupeek.maid_booking.repository.MaidRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultMaidSearch implements MaidSearch {
    private final MaidRepository maidRepository;

    public DefaultMaidSearch(MaidRepository maidRepository) { this.maidRepository = maidRepository; }

    @Override
    public List<Maid> search(SearchCriteria criteria) {
        List<MaidFilter> filters = new ArrayList<>();
        if (criteria.locality() != null) filters.add(m -> m.getLocality().equalsIgnoreCase(criteria.locality()));
        if (criteria.serviceType() != null) filters.add(m -> m.getServices().contains(criteria.serviceType()));
        if (criteria.gender() != null) filters.add(m -> m.getGender() == criteria.gender());
        if (criteria.minimumRating() != null) filters.add(m -> m.getRating() >= criteria.minimumRating());
        if (criteria.maximumPrice() != null) filters.add(m -> m.getHourlyRate().compareTo(criteria.maximumPrice()) <= 0);
        if (criteria.skills() != null && !criteria.skills().isEmpty()) filters.add(m -> m.getSkills().containsAll(new HashSet<>(criteria.skills())));
        if (criteria.requestedSlot() != null) filters.add(m -> m.isAvailable(criteria.requestedSlot()));

        return maidRepository.findAll().stream()
                .filter(m -> filters.stream().allMatch(f -> f.matches(m)))
                .collect(Collectors.toList());
    }
}
