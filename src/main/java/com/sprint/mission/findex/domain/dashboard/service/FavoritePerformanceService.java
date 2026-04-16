package com.sprint.mission.findex.domain.dashboard.service;

import com.sprint.mission.findex.domain.dashboard.dto.FavoritePerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.dto.PerformancePeriodType;
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
public class FavoritePerformanceService {

    private static final LocalDate Min_Date = LocalDate.of(1900, 1, 1);

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    public List<FavoritePerformanceResponse> getFavoritePerformance(
            PerformancePeriodType periodType
    ) {
        return indexInfoRepository.findAll().stream()
                .filter(indexInfo -> Boolean.TRUE.equals(indexInfo.getFavorite()))
                .map(indexInfo -> toFavoritePerformance(indexInfo, periodType))
                .filter(Objects::nonNull)
                .toList();
    }

    private FavoritePerformanceResponse toFavoritePerformance(
        IndexInfo indexInfo,
        PerformancePeriodType periodType
    ) {
        List<IndexData> indexDataList = indexDataRepository.findByIndexInfoIdAndBaseDateBetween(
                indexInfo.getId(),
                Min_Date,
                LocalDate.now()
        );

        if (indexDataList.isEmpty()) {
            return null;
        }

        List<IndexData> sorted = indexDataList.stream()
                .sorted(Comparator.comparing(IndexData::getBaseDate).reversed())
                .toList();

        IndexData currentData = sorted.get(0);
        LocalDate targetDate = getTargetDate(currentData.getBaseDate(), periodType);
        IndexData beforeData = findClosestBeforeOrEqual(sorted, targetDate);

        if (beforeData == null) {
            return null;
        }

        BigDecimal currentPrice = currentData.getClosingPrice();
        BigDecimal beforePrice = beforeData.getClosingPrice();
        BigDecimal versus = currentPrice.subtract(beforePrice);
        BigDecimal fluctuationRate = calculateFluctuationRate(currentPrice, beforePrice);

        return new FavoritePerformanceResponse(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                versus,
                fluctuationRate,
                currentPrice,
                beforePrice
        );
    }

    private LocalDate getTargetDate(LocalDate currentDate, PerformancePeriodType periodType) {
        return switch (periodType) {
            case DAILY -> currentDate.minusDays(1);
            case WEEKLY -> currentDate.minusWeeks(1);
            case MONTHLY -> currentDate.minusMonths(1);
        };
    }

    private IndexData findClosestBeforeOrEqual(List<IndexData> sorted, LocalDate targetDate) {
        for (IndexData indexData : sorted) {
            if (!indexData.getBaseDate().isAfter(targetDate)) {
                return indexData;
            }
        }
        return null;
    }

    private BigDecimal calculateFluctuationRate(
            BigDecimal currentPrice,
            BigDecimal beforePrice
    ) {
        if (currentPrice == null || beforePrice == null) {
            return null;
        }

        if (BigDecimal.ZERO.compareTo(beforePrice) == 0) {
            return null;
        }

        return currentPrice.subtract(beforePrice)
                .divide(beforePrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);
    }
}
