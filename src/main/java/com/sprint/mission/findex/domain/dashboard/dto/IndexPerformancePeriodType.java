package com.sprint.mission.findex.domain.dashboard.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "성과 기간 유형")
public enum IndexPerformancePeriodType {
    DAILY,
    WEEKLY,
    MONTHLY
}
