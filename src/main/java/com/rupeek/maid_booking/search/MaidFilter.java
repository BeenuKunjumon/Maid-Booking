package com.rupeek.maid_booking.search;

import com.rupeek.maid_booking.domain.Maid;

@FunctionalInterface
public interface MaidFilter {
    boolean matches(Maid maid);
}
