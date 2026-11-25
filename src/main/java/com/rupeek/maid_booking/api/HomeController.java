package com.rupeek.maid_booking.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "service", "Maid Booking Service",
                "status", "running",
                "maids", "/api/maids",
                "maidSearch", "/api/maids/search",
                "bookings", "/api/bookings"
        );
    }
}
