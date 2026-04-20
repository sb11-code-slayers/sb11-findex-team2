package com.sprint.mission.findex.domain.dashboard.service;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.dto.RankedIndexPerformanceResponse;
import com.sprint.mission.findex.domain.indexdata.entity.IndexData;
import com.sprint.mission.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.mission.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.mission.findex.domain.indexinfo.repository.IndexInfoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexPerformanceRankService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    public List<RankedIndexPerformanceResponse> getIndexPerformanceRank(
            UUID indexInfoId,
            IndexPerformancePeriodType periodType,
            Integer limit
    ) {
        int safeLimit = (limit == null || limit < 1)
                ? DEFAULT_LIMIT
                : Math.min(limit, MAX_LIMIT);

        List<IndexInfo> targetIndexInfos = getTargetIndexInfos(indexInfoId);

        List<IndexPerformanceResponse> performances = targetIndexInfos.stream()
                .map(indexInfo -> toIndexPerformance(indexInfo, periodType))
                .filter(Objects::nonNull)
                .sorted(
                        Comparator.comparing(
                                IndexPerformanceResponse::fluctuationRate,
                                Comparator.nullsLast(BigDecimal::compareTo)
                        ).reversed()
                )
                .toList();

        return assignRanks(performances).stream()
                .limit(safeLimit)
                .toList();
    }

    private List<IndexInfo> getTargetIndexInfos(UUID indexInfoId) {
        if (indexInfoId == null) {
            return indexInfoRepository.findAll();
        }

        return indexInfoRepository.findById(indexInfoId)
                .map(List::of)
                .orElseGet(List::of);
    }

    private IndexPerformanceResponse toIndexPerformance(
            IndexInfo indexInfo,
            IndexPerformancePeriodType periodType
    ) {
        IndexData currentData = indexDataRepository
                .findFirstByIndexInfoIdOrderByBaseDateDesc(indexInfo.getId())
                .orElse(null);

        if (currentData == null) {
            return null;
        }

        LocalDate targetDate = getTargetDate(currentData.getBaseDate(), periodType);

        IndexData beforeData = indexDataRepository
                .findFirstByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
                        indexInfo.getId(),
                        targetDate
                )
                .orElse(null);
        if (beforeData == null) {
            return null;
        }

        BigDecimal currentPrice = currentData.getClosingPrice();
        BigDecimal beforePrice = beforeData.getClosingPrice();
        BigDecimal versus = currentPrice.subtract(beforePrice);
        BigDecimal fluctuationRate = calculateFluctuationRate(currentPrice, beforePrice);

        return new IndexPerformanceResponse(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                versus,
                fluctuationRate,
                currentPrice,
                beforePrice
        );
    }

    private LocalDate getTargetDate(
            LocalDate currentDate,
            IndexPerformancePeriodType periodType
    ) {
        return switch (periodType) {
            case DAILY -> currentDate.minusDays(1);
            case WEEKLY -> currentDate.minusWeeks(1);
            case MONTHLY -> currentDate.minusMonths(1);
        };
    }

    private IndexData findClosestBeforeOrEqual(
            List<IndexData> sorted,
            LocalDate targetDate
    ) {
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
    private List<RankedIndexPerformanceResponse> assignRanks(
            List<IndexPerformanceResponse> performances
    ) {
        List<RankedIndexPerformanceResponse> result = new ArrayList<>();

        for (int i = 0; i < performances.size(); i++) {
            result.add(new RankedIndexPerformanceResponse(
                    performances.get(i),
                    i + 1
            ));
        }

        return result;
    }
}
