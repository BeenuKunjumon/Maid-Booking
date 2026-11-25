package com.rupeek.maid_booking.payment;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {
    @Override public String charge(String bookingId, BigDecimal amount, String paymentMethodType) {
        return "pay_" + UUID.randomUUID();
    }
    @Override public String refund(String paymentId, BigDecimal amount) {
        return "refund_" + UUID.randomUUID();
    }
}
