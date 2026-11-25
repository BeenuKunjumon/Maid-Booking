package com.rupeek.maid_booking.api;

import com.rupeek.maid_booking.domain.*;
import com.rupeek.maid_booking.search.*;
import com.rupeek.maid_booking.service.MaidService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/maids")
public class MaidController {
    private final MaidService maidService;
    private final MaidSearch maidSearch;

    public MaidController(MaidService maidService, MaidSearch maidSearch) {
        this.maidService = maidService;
        this.maidSearch = maidSearch;
    }

    public record AddMaidRequest(@NotBlank String name, @NotBlank String locality, Gender gender,
                                 Set<ServiceType> services, Set<String> skills,
                                 @Positive BigDecimal hourlyRate, @DecimalMin("0") @DecimalMax("5") double rating,
                                 Set<AvailabilityRequest> availability) {
        public record AvailabilityRequest(LocalDateTime start, LocalDateTime end) {}
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Maid add(@Valid @RequestBody AddMaidRequest request) {
        Set<AvailabilityWindow> windows = new HashSet<>();
        if (request.availability() != null) {
            request.availability().forEach(a -> windows.add(new AvailabilityWindow(new TimeSlot(a.start(), a.end()))));
        }
        Maid maid = new Maid(UUID.randomUUID().toString(), request.name(), request.locality(), request.gender(),
                request.services(), request.skills(), request.hourlyRate(), request.rating(), windows);
        return maidService.add(maid);
    }

    @GetMapping("/search")
    public List<Maid> search(@RequestParam(required=false) String locality,
                             @RequestParam(required=false) ServiceType serviceType,
                             @RequestParam(required=false) Gender gender,
                             @RequestParam(required=false) Double minimumRating,
                             @RequestParam(required=false) BigDecimal maximumPrice,
                             @RequestParam(required=false) Set<String> skills,
                             @RequestParam(required=false) LocalDateTime start,
                             @RequestParam(required=false) LocalDateTime end) {
        TimeSlot slot = (start != null && end != null) ? new TimeSlot(start, end) : null;
        return maidSearch.search(new SearchCriteria(locality, serviceType, gender, minimumRating,
                maximumPrice, skills == null ? null : List.copyOf(skills), slot));
    }
}
