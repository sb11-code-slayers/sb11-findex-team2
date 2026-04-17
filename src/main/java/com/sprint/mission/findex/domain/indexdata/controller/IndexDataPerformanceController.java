package com.sprint.mission.findex.domain.indexdata.controller;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.service.IndexPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
public class IndexDataPerformanceController {

    private final IndexPerformanceService indexPerformanceService;

    @GetMapping("/favorite")
    public List<IndexPerformanceResponse> getFavoriteIndexPerformance(
            @RequestParam IndexPerformancePeriodType periodType
    ) {
        return indexPerformanceService.getFavoriteIndexPerformance(periodType);
    }
}