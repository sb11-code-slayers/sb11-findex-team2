package com.sprint.mission.findex.domain.dashboard.service;

import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformanceResponse;
import com.sprint.mission.findex.domain.dashboard.dto.IndexPerformancePeriodType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardPerformanceService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    public List<IndexPerformanceResponse> getFavoriteIndexPerformance(
            IndexPerformancePeriodType periodType
    ) {
        List<IndexInfo> favoriteIndexInfos = indexInfoRepository.findAllByFavoriteTrue();
        if (favoriteIndexInfos.isEmpty()) {
            return List.of();
        }

        List<UUID> indexInfoIds = favoriteIndexInfos.stream()
                .map(IndexInfo::getId)
                .toList();

        Map<UUID, IndexData> currentDataByIndexId = indexDataRepository.findLatestByIndexInfoIds(indexInfoIds)
                .stream()
                .collect(toMap(indexData -> indexData.getIndexInfo().getId(), identity()));

        Map<UUID, IndexData> beforeDataByIndexId = getBeforeDataByIndexId(currentDataByIndexId, periodType);

        return favoriteIndexInfos.stream()
                .map(indexInfo -> toIndexPerformance(
                        indexInfo,
                        currentDataByIndexId.get(indexInfo.getId()),
                        beforeDataByIndexId.get(indexInfo.getId())
                ))
                .filter(Objects::nonNull)
                .toList();
    }

    private IndexPerformanceResponse toIndexPerformance(
        IndexInfo indexInfo,
        IndexData currentData,
        IndexData beforeData
    ) {
        if (currentData == null || beforeData == null) {
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

    private Map<UUID, IndexData> getBeforeDataByIndexId(
            Map<UUID, IndexData> currentDataByIndexId,
            IndexPerformancePeriodType periodType
    ) {
        Map<LocalDate, List<UUID>> indexIdsByTargetDate = currentDataByIndexId.values().stream()
                .collect(groupingBy(
                        indexData -> getTargetDate(indexData.getBaseDate(), periodType),
                        mapping(indexData -> indexData.getIndexInfo().getId(), toList())
                ));

        Map<UUID, IndexData> beforeDataByIndexId = new HashMap<>();
        indexIdsByTargetDate.forEach((targetDate, indexInfoIds) ->
                indexDataRepository.findLatestByIndexInfoIdsAndBaseDateLessThanEqual(indexInfoIds, targetDate)
                        .forEach(indexData ->
                                beforeDataByIndexId.put(indexData.getIndexInfo().getId(), indexData)
                        )
        );

        return beforeDataByIndexId;
    }

    private LocalDate getTargetDate(LocalDate currentDate, IndexPerformancePeriodType periodType) {
        return switch (periodType) {
            case DAILY -> currentDate.minusDays(1);
            case WEEKLY -> currentDate.minusWeeks(1);
            case MONTHLY -> currentDate.minusMonths(1);
        };
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
