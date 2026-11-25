package com.rupeek.maid_booking.payment;

import org.springframework.stereotype.Component;

@Component
public class WalletPaymentMethod implements PaymentMethod {
    @Override public String type() { return "WALLET"; }
}
