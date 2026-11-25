package com.rupeek.maid_booking.search;

import com.rupeek.maid_booking.domain.Maid;
import java.util.List;

public interface MaidSearch {
    List<Maid> search(SearchCriteria criteria);
}
