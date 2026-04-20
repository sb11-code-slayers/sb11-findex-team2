package com.sprint.mission.findex.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "순위가 포함된 지수 성과 정보 응답")
public record RankedIndexPerformanceResponse(
    @Schema(description = "지수 성과 정보")
    IndexPerformanceResponse performance,

    @Schema(description = "순위", example = "1")
    Integer rank
) {
}
