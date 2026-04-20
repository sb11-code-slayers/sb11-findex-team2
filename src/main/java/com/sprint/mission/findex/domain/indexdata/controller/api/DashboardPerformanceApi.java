package com.sprint.mission.findex.domain.indexdata.controller.api;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformanceResponse;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public interface DashboardPerformanceApi {

    @Operation(
            summary = "관심 지수 성과 조회",
            description = "즐겨찾기로 등록된 지수들의 성과를 조회합니다.",
            operationId = "getFavoriteIndexPerformance"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "관심 지수 성과 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = IndexPerformanceResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    List<IndexPerformanceResponse> getFavoriteIndexPerformance(
            @Parameter(description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)")
            @RequestParam(defaultValue = "DAILY") IndexPerformancePeriodType periodType
    );
}