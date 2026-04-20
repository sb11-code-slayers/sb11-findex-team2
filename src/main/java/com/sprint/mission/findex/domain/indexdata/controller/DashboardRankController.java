package com.sprint.mission.findex.domain.indexdata.controller;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.dto.RankedIndexPerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.service.DashboardPerformanceRankService;
import com.sprint.mission.findex.domain.indexdata.controller.api.DashboardRankApi;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
public class DashboardRankController implements DashboardRankApi {

    private final DashboardPerformanceRankService dashboardPerformanceRankService;

    @Override
    @GetMapping("/rank")
    public ResponseEntity<List<RankedIndexPerformanceResponse>> getIndexPerformanceRank(
            UUID indexInfoId,
            IndexPerformancePeriodType periodType,
            Integer limit
    ) {
        return ResponseEntity.ok(
                dashboardPerformanceRankService.getIndexPerformanceRank(indexInfoId, periodType, limit)
        );
    }
}
