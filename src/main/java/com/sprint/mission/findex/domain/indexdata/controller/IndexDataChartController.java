package com.sprint.mission.findex.domain.indexdata.controller;

import com.sprint.mission.findex.domain.dashboard.dto.IndexChartPeriodType;
import com.sprint.mission.findex.domain.dashboard.dto.IndexChartResponse;
import com.sprint.mission.findex.domain.dashboard.service.IndexChartService;
import com.sprint.mission.findex.domain.indexdata.controller.api.IndexDataChartApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataChartController implements IndexDataChartApi {

    private final IndexChartService indexChartService;

    @Override
    @GetMapping("/{id}/chart")
    public IndexChartResponse getIndexChart(
            UUID id,
            IndexChartPeriodType periodType
    ) {
        return indexChartService.getIndexChart(id, periodType);
    }
}
