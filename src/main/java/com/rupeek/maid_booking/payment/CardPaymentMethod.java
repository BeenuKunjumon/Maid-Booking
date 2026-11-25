package com.rupeek.maid_booking.payment;

import org.springframework.stereotype.Component;

@Component
public class CardPaymentMethod implements PaymentMethod {
    @Override public String type() { return "CARD"; }
}
