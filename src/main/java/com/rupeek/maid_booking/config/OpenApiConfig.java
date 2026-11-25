package com.rupeek.maid_booking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Maid Booking API",
        version = "1.0",
        description = "REST API for maid discovery, bookings, payments, and cancellations",
        contact = @Contact(name = "Maid Booking Platform")
))
public class OpenApiConfig {}
