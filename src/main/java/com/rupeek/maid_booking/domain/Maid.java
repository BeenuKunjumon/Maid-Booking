package com.rupeek.maid_booking.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Maid {
    private final String id;
    private final String name;
    private final String locality;
    private final Gender gender;
    private final Set<ServiceType> services;
    private final Set<String> skills;
    private final BigDecimal hourlyRate;
    private final double rating;
    private final Set<AvailabilityWindow> availability;

    public Maid(String id, String name, String locality, Gender gender, Set<ServiceType> services,
                Set<String> skills, BigDecimal hourlyRate, double rating,
                Set<AvailabilityWindow> availability) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (locality == null || locality.isBlank()) throw new IllegalArgumentException("Locality is required");
        if (hourlyRate == null || hourlyRate.signum() <= 0) throw new IllegalArgumentException("Valid hourly rate is required");
        if (rating < 0 || rating > 5) throw new IllegalArgumentException("Rating must be between 0 and 5");
        this.id = id;
        this.name = name;
        this.locality = locality;
        this.gender = gender;
        this.services = new HashSet<>(services);
        this.skills = new HashSet<>(skills);
        this.hourlyRate = hourlyRate;
        this.rating = rating;
        this.availability = new HashSet<>(availability);
    }

    public boolean isAvailable(TimeSlot requested) {
        return availability.stream().anyMatch(window -> requested.isWithin(window.slot()));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocality() { return locality; }
    public Gender getGender() { return gender; }
    public Set<ServiceType> getServices() { return Set.copyOf(services); }
    public Set<String> getSkills() { return Set.copyOf(skills); }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public double getRating() { return rating; }
    public Set<AvailabilityWindow> getAvailability() { return Set.copyOf(availability); }
}
