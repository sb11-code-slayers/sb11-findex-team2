package com.sprint.mission.findex.domain.dashboard.service;

import com.sprint.mission.findex.domain.dashboard.dto.DashboardSummaryItem;
import com.sprint.mission.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.sprint.mission.findex.domain.indexdata.entity.IndexData;
import com.sprint.mission.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.mission.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.mission.findex.domain.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private static final LocalDate Min_Date = LocalDate.of(1900, 1, 1);
    private static final LocalDate Max_Date = LocalDate.of(2100, 12, 31);

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    public DashboardSummaryResponse getSummary() {
        List<DashboardSummaryItem> items = indexInfoRepository.findAll().stream()
                .filter(indexInfo -> Boolean.TRUE.equals(indexInfo.getFavorite()))
                .map(this::toSummaryItem)
                .filter(Objects::nonNull)
                .toList();

        return new DashboardSummaryResponse(items);
    }

    private DashboardSummaryItem toSummaryItem(IndexInfo indexinfo) {
        List<IndexData> indexDataList = indexDataRepository.findByIndexInfoIdAndBaseDateBetween(
                indexinfo.getId(),
                Min_Date,
                Max_Date
        );

        if (indexDataList.isEmpty()) {
            return null;
        }

        List<IndexData> sorted = indexDataList.stream()
                .sorted(Comparator.comparing(IndexData::getBaseDate).reversed())
                .toList();

        IndexData currentData = sorted.get(0);
        IndexData baseData = sorted.size() > 1 ? sorted.get(1) : null;

        BigDecimal baseClosingPrice = baseData != null ? baseData.getClosingPrice() : null;
        LocalDate baseDate = baseData != null ? baseData.getBaseDate() : null;

        BigDecimal performanceRate = calculatePerformanceRate(
                currentData.getClosingPrice(),
                baseClosingPrice
        );

        return new DashboardSummaryItem(
                indexinfo.getId(),
                indexinfo.getIndexName(),
                indexinfo.getIndexClassification(),
                currentData.getBaseDate(),
                currentData.getClosingPrice(),
                baseDate,
                baseClosingPrice,
                performanceRate
        );
    }

    private BigDecimal calculatePerformanceRate(BigDecimal currentClosingPrice, BigDecimal baseClosingPrice) {
        if (currentClosingPrice == null || baseClosingPrice == null) {
            return null;
        }

        if (BigDecimal.ZERO.compareTo(baseClosingPrice) == 0) {
            return null;
        }

        return currentClosingPrice.subtract(baseClosingPrice).divide(baseClosingPrice, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
}
