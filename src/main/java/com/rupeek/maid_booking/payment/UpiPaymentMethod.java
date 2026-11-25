package com.rupeek.maid_booking.payment;

import org.springframework.stereotype.Component;

@Component
public class UpiPaymentMethod implements PaymentMethod {
    @Override public String type() { return "UPI"; }
}
