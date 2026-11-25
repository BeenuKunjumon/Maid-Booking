# Maid Booking Service

A runnable Spring Boot 4 service demonstrating maid discovery, booking, payment, cancellation, and overlap-safe inventory handling.

## Run

Requirements: Java 17+

```bash
./mvnw spring-boot:run
```

Run tests:

```bash
./mvnw test
```

The service starts at `http://localhost:8080`.

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`; the OpenAPI document is at `http://localhost:8080/v3/api-docs`.

## API examples

Add a maid:

```bash
curl -X POST http://localhost:8080/api/maids -H 'Content-Type: application/json' -d '{"name":"Asha","locality":"Indiranagar","gender":"FEMALE","services":["CLEANING"],"skills":["dusting"],"hourlyRate":300,"rating":4.8,"availability":[{"start":"2026-12-01T08:00:00","end":"2026-12-01T18:00:00"}]}'
```

Search:

```bash
curl 'http://localhost:8080/api/maids/search?locality=Indiranagar&serviceType=CLEANING&minimumRating=4&start=2026-12-01T09:00:00&end=2026-12-01T11:00:00'
```

The add-maid response contains the generated maid ID. Use it for these requests:

```bash
curl -X POST http://localhost:8080/api/bookings/scheduled -H 'Content-Type: application/json' -d '{"customerId":"customer-1","maidId":"MAID_ID","slot":{"start":"2026-12-01T09:00:00","end":"2026-12-01T11:00:00"}}'

curl -X POST http://localhost:8080/api/bookings/BOOKING_ID/payments -H 'Content-Type: application/json' -H 'Idempotency-Key: payment-key-001' -d '{"method":"UPI"}'

curl -X POST 'http://localhost:8080/api/bookings/BOOKING_ID/cancel?occurrenceStart=2026-12-01T09:00:00'
```

For recurring bookings, post to `/api/bookings/recurring` with `startDate`, `days`, `startTime`, `durationMinutes`, and `endDate`.

## Design decisions

- Domain objects validate invariants and preserve state transitions.
- `MaidFilter` and `MaidSearch` provide composable discovery criteria.
- `BookingStrategy` implementations isolate instant, scheduled, and recurring occurrence resolution.
- `PaymentMethod` and `PaymentGateway` separate payment choice from gateway integration.
- `CancellationPolicy` is injectable and pluggable.
- `PricingStrategy` supports standard and weekend dynamic pricing; the dynamic strategy applies a 20% surcharge on weekends.
- Repositories are interfaces with thread-safe in-memory implementations.
- Booking creation is synchronized per maid and rejects availability violations and overlapping confirmed occurrences.
- A recurring booking is represented as a set of concrete occurrences. Cancelling one occurrence removes it; cancelling the series marks all occurrences cancelled.
- Payment calls support booking-level and `Idempotency-Key`-based duplicate protection.

## Assumptions

- Local in-memory state is reset on restart.
- Availability windows are absolute Java local-date/time ranges.
- The cancellation policy refunds 100% at least 24 hours before a slot, 50% at least 6 hours before, and 0% otherwise.
- The mock payment gateway always approves payment and refund calls.

With more time: persist repositories to a database, add H2 integration tests and OpenAPI, strengthen per-occurrence payments/refunds, and add a booking ledger for all monetary operations.
