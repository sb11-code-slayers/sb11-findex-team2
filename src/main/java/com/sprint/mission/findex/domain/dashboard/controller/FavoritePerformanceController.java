package com.sprint.mission.findex.domain.dashboard.controller;

import com.sprint.mission.findex.domain.dashboard.dto.FavoritePerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.dto.PerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.service.FavoritePerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
public class FavoritePerformanceController {

    private final FavoritePerformanceService favoritePerformanceService;

    @GetMapping("/favorite")
    public List<FavoritePerformanceResponse> getFavoritePerformance(
            @RequestParam PerformancePeriodType periodType
    ) {
        return favoritePerformanceService.getFavoritePerformance(periodType);
    }
}