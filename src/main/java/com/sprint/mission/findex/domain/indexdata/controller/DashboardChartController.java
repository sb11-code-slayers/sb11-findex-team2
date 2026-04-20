package com.sprint.mission.findex.domain.indexdata.controller;

import com.sprint.mission.findex.domain.dashboard.dto.IndexChartResponse;
import com.sprint.mission.findex.domain.dashboard.service.DashboardChartService;
import com.sprint.mission.findex.domain.dashboard.dto.IndexChartPeriodType;
import com.sprint.mission.findex.domain.indexdata.controller.api.DashboardChartApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class DashboardChartController implements DashboardChartApi {

    private final DashboardChartService dashboardChartService;

    @Override
    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartResponse> getIndexChart(
            UUID id,
            IndexChartPeriodType periodType
    ){
        return ResponseEntity.ok(dashboardChartService.getIndexChart(id, periodType));
    }
}
