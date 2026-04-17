package com.sprint.mission.findex.domain.indexinfo.repository;

import com.sprint.mission.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.mission.findex.domain.indexinfo.repository.querydsl.IndexInfoCustomRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID>,
    IndexInfoCustomRepository {

  boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

  Optional<IndexInfo> findByIndexClassificationAndIndexName(String indexClassification, String indexName);
}
