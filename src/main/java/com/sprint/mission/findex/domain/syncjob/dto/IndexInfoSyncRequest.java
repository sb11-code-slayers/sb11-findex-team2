package com.sprint.mission.findex.domain.syncjob.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(name = "IndexInfoSyncRequest", description = "지수 정보 연동 요청")
public record IndexInfoSyncRequest(

    @Schema(description = "조회 기준 날짜", example = "2024-01-01")
    @NotNull(message = "기준 날짜(targetDate)는 필수입니다.")
    LocalDate targetDate
) {
}
