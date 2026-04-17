package com.sprint.mission.findex.domain.dashboard.dto;

import java.util.UUID;
import java.math.BigDecimal;

public record IndexPerformanceResponse(
    UUID indexInfoId,
    String indexClassification,
    String indexName,
    BigDecimal versus,
    BigDecimal fluctuationRate,
    BigDecimal currentPrice,
    BigDecimal beforePrice
) {
}

