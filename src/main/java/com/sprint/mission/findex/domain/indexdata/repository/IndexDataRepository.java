package com.sprint.mission.findex.domain.indexdata.repository;

import com.sprint.mission.findex.domain.indexdata.entity.IndexData;
import com.sprint.mission.findex.domain.indexdata.repository.querydsl.IndexDataCustomRepository;
import com.sprint.mission.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID>,
    IndexDataCustomRepository {

  boolean existsByIndexInfoAndBaseDate(IndexInfo indexInfo, LocalDate baseDate);

  Optional<IndexData> findByIndexInfoAndBaseDate(IndexInfo indexInfo, LocalDate baseDate);

  List<IndexData> findByIndexInfoIdAndBaseDateBetween(UUID indexInfoId, LocalDate from, LocalDate to);

  List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(UUID indexInfoId, LocalDate from, LocalDate to);

  List<IndexData> findByBaseDateBetween(LocalDate from, LocalDate to);

  Optional<IndexData> findFirstByIndexInfoIdOrderByBaseDateDesc(UUID indexInfoId);

  Optional<IndexData> findFirstByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
      UUID indexInfoId,
      LocalDate baseDate
  );

  @Query("""
      select d
      from IndexData d
      join fetch d.indexInfo i
      where i.id in :indexInfoIds
        and d.baseDate = (
            select max(d2.baseDate)
            from IndexData d2
            where d2.indexInfo.id = i.id
        )
      """)
  List<IndexData> findLatestByIndexInfoIds(
      @Param("indexInfoIds") List<UUID> indexInfoIds
  );

  @Query("""
      select d
      from IndexData d
      join fetch d.indexInfo i
      where i.id in :indexInfoIds
        and d.baseDate = (
            select max(d2.baseDate)
            from IndexData d2
            where d2.indexInfo.id = i.id
              and d2.baseDate <= :targetDate
        )
      """)
  List<IndexData> findLatestByIndexInfoIdsAndBaseDateLessThanEqual(
      @Param("indexInfoIds") List<UUID> indexInfoIds,
      @Param("targetDate") LocalDate targetDate
  );

  @Query("SELECT d FROM IndexData d JOIN FETCH d.indexInfo WHERE " +
      "(:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId) AND " +
      "(:startDate IS NULL OR d.baseDate >= :startDate) AND " +
      "(:endDate IS NULL OR d.baseDate <= :endDate) " +
      "ORDER BY d.baseDate DESC")
  @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
  Stream<IndexData> streamForExport(
      @Param("indexInfoId") UUID indexInfoId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
}