package com.sprint.mission.findex.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "차트 기간 유형 (MONTHLY, QUARTERLY, YEARLY)")
public enum IndexChartPeriodType {
    MONTHLY,
    QUARTERLY,
    YEARLY
}
