package com.sprint.mission.findex.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "지수 차트 데이터 응답")
public record IndexChartResponse(
        @Schema(description = "지수 정보 ID", example = "1")
        UUID indexInfoId,

        @Schema(description = "지수 분류명", example = "KOSPI시리즈")
        String indexClassification,

        @Schema(description = "지수명", example = "IT 서비스")
        String indexName,

        @Schema(description = "차트 기간 유형 (MONTHLY, QUARTERLY, YEARLY)")
        IndexChartPeriodType periodType,

        @Schema(description = "차트 데이터 목록")
        List<ChartDataPoint> dataPoints,

        @Schema(description = "5일 이동평균선 데이터 목록")
        List<ChartDataPoint> ma5DataPoints,

        @Schema(description = "20일 이동평균선 데이터 목록")
        List<ChartDataPoint> ma20DataPoints
) {
}
