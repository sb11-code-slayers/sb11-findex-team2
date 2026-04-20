package com.sprint.mission.findex.domain.indexdata.controller.api;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.dto.RankedIndexPerformanceResponse;
import com.sprint.mission.findex.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public interface DashboardRankApi {

    @Operation(
            summary = "지수 성과 랭킹 조회",
            description = "지수의 성과 분석 랭킹을 조회합니다.",
            operationId = "getIndexPerformanceRank"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성과 랭킹 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = RankedIndexPerformanceResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 기간 유형 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<List<RankedIndexPerformanceResponse>> getIndexPerformanceRank(
            @Parameter(description = "지수 정보 ID")
            @RequestParam(required = false) UUID indexInfoId,

            @Parameter(description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)")
            @RequestParam(name = "periodType", defaultValue = "DAILY")
            IndexPerformancePeriodType periodType,

            @Parameter(description = "최대 랭킹 수")
            @RequestParam(name = "limit", defaultValue = "10")
            Integer limit
    );
}