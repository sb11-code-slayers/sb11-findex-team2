package com.sprint.mission.findex.domain.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DashboardSummaryItem(
        UUID indexInfoId,
        String indexName,
        String indexClassification,
        LocalDate currentDate,
        BigDecimal currentClosingPrice,
        LocalDate baseDate,
        BigDecimal baseClosingPrice,
        BigDecimal performanceRate
) {
}
