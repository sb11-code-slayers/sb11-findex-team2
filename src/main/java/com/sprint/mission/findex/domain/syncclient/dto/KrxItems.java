package com.sprint.mission.findex.domain.syncclient.dto;

import java.util.Collections;
import java.util.List;

public record KrxItems(
        List<IndexDataApiResponse> item
) {
    public KrxItems {
        item = item == null ? Collections.emptyList() : item;
    }
}
