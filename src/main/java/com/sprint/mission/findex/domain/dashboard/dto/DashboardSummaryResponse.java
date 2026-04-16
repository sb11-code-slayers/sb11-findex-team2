package com.sprint.mission.findex.domain.dashboard.dto;

import java.util.List;

public record DashboardSummaryResponse(
        List<DashboardSummaryItem> items
) {
}
