package com.sprint.mission.findex.domain.syncclient.client;

import com.sprint.mission.findex.domain.syncclient.dto.IndexDataApiResponse;
import java.time.LocalDate;
import java.util.List;

public interface KrxOpenApiClient {
    List<IndexDataApiResponse> fetchByDateRange(String indexName, LocalDate from, LocalDate to);
    List<IndexDataApiResponse> fetchByDate(LocalDate date);
}