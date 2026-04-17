package com.sprint.mission.findex.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
import java.math.BigDecimal;

@Schema(description = "지수 성과 정보 응답")
public record IndexPerformanceResponse(

    @Schema(description = "지수 정보 ID", example = "1")
    UUID indexInfoId,

    @Schema(description = "지수 분류명", example = "KOSPI시리즈")
    String indexClassification,

    @Schema(description = "지수명", example = "IT 서비스")
    String indexName,

    @Schema(description = "단위 기간 대비 등락", example = "50.5")
    BigDecimal versus,

    @Schema(description = "단위 기간 대비 등락률", example = "1.8")
    BigDecimal fluctuationRate,

    @Schema(description = "현재가", example = "2850.75")
    BigDecimal currentPrice,

    @Schema(description = "단위 기간 전 값", example = "2800.25")
    BigDecimal beforePrice
) {
}

