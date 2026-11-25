package com.rupeek.maid_booking.payment;

import java.math.BigDecimal;

public interface PaymentGateway {
    String charge(String bookingId, BigDecimal amount, String paymentMethodType);
    String refund(String paymentId, BigDecimal amount);
}
