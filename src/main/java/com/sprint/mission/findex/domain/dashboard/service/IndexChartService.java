package com.sprint.mission.findex.domain.dashboard.service;

import com.sprint.mission.findex.domain.dashboard.dto.ChartDataPoint;
import com.sprint.mission.findex.domain.dashboard.dto.IndexChartPeriodType;
import com.sprint.mission.findex.domain.dashboard.dto.IndexChartResponse;
import com.sprint.mission.findex.domain.indexdata.entity.IndexData;
import com.sprint.mission.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.mission.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.mission.findex.domain.indexinfo.repository.IndexInfoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexChartService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    public IndexChartResponse getIndexChart(
            UUID id,
            IndexChartPeriodType periodType
    ) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("지수 정보를 찾을 수 없습니다."));

        LocalDate fromDate = getFromDate(periodType);

        List<IndexData> sortedIndexData = indexDataRepository
                .findByIndexInfoIdAndBaseDateBetween(id, fromDate, LocalDate.now())
                .stream()
                .sorted(Comparator.comparing(IndexData::getBaseDate))
                .toList();

        List<ChartDataPoint> dataPoints = toChartDataPoints(sortedIndexData);
        List<ChartDataPoint> ma5DataPoints = calculateMovingAverage(sortedIndexData, 5);
        List<ChartDataPoint> ma20DataPoints = calculateMovingAverage(sortedIndexData, 20);

        return new IndexChartResponse(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                periodType,
                dataPoints,
                ma5DataPoints,
                ma20DataPoints
        );
    }

    private LocalDate getFromDate(IndexChartPeriodType periodType) {
        LocalDate today = LocalDate.now();

        return switch (periodType) {
            case MONTHLY -> today.minusMonths(1);
            case QUARTERLY -> today.minusMonths(3);
            case YEARLY -> today.minusYears(1);
        };
    }

    private List<ChartDataPoint> toChartDataPoints(List<IndexData> sortedIndexData) {
        return sortedIndexData.stream()
                .map(indexData -> new ChartDataPoint(
                        indexData.getBaseDate(),
                        indexData.getClosingPrice()
                ))
                .toList();
    }

    private List<ChartDataPoint> calculateMovingAverage(
            List<IndexData> sortedIndexData,
            int windowSize
    ) {
        List<ChartDataPoint> result = new ArrayList<>();

        for (int i = 0; i < sortedIndexData.size(); i++) {
            if (i + 1 < windowSize) {
                continue;
            }

            BigDecimal sum = BigDecimal.ZERO;

            for (int j = i - windowSize + 1; j <= i; j++) {
                sum = sum.add(sortedIndexData.get(j).getClosingPrice());
            }

            BigDecimal average = sum.divide(
                    BigDecimal.valueOf(windowSize),
                    4,
                    RoundingMode.HALF_UP
            );

            result.add(new ChartDataPoint(
                    sortedIndexData.get(i).getBaseDate(),
                    average
            ));
        }

        return result;
    }
}
