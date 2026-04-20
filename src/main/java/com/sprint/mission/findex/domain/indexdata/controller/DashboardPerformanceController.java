package com.sprint.mission.findex.domain.indexdata.controller;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.service.DashboardPerformanceService;
import com.sprint.mission.findex.domain.indexdata.controller.api.DashboardPerformanceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
public class DashboardPerformanceController implements DashboardPerformanceApi {

    private final DashboardPerformanceService dashboardPerformanceService;

    @Override
    @GetMapping("/favorite")
    public List<IndexPerformanceResponse> getFavoriteIndexPerformance(
            @RequestParam(name = "periodType", defaultValue = "DAILY")
            IndexPerformancePeriodType periodType
    ) {
        return dashboardPerformanceService.getFavoriteIndexPerformance(periodType);
    }
}