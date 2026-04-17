package com.sprint.mission.findex.domain.syncjob.controller.api;

import com.sprint.mission.findex.domain.syncjob.dto.IndexDataSyncRequest;
import com.sprint.mission.findex.domain.syncjob.dto.IndexInfoSyncRequest;
import com.sprint.mission.findex.domain.syncjob.dto.SyncJobResponse;
import com.sprint.mission.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.sprint.mission.findex.global.common.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = "Sync Job", description = "지수 데이터 연동 및 이력 관리 API (M4)")
public interface SyncJobApi {

  @Operation(summary = "지수 정보 연동", description = "KRX Open API에서 기준 날짜의 전체 지수 정보를 조회하여 DB에 생성 또는 갱신하고 SyncJob 이력을 저장합니다.")
  ResponseEntity<String> syncIndexInfos(
      @Valid @RequestBody IndexInfoSyncRequest request,
      HttpServletRequest servletRequest);

  @Operation(summary = "지수 데이터 수동 연동", description = "지정한 기간(baseDateFrom ~ baseDateTo)의 하나 이상의 지수 데이터를 외부 API로부터 연동합니다.")
  ResponseEntity<String> syncIndexData(
      @Valid @RequestBody IndexDataSyncRequest request,
      HttpServletRequest servletRequest);

  @Operation(summary = "연동 작업 목록 조회", description = "정렬값(cursor)과 ID(idAfter) 기반 복합 커서를 사용하여 연동 이력을 페이징 조회합니다.")
  ResponseEntity<CursorPageResponse<SyncJobResponse>> getSyncJobHistory(

      @Valid @ParameterObject @ModelAttribute SyncJobSearchCondition condition,

      @Parameter(description = "커서 (다음 페이지 시작점)")
      @RequestParam(required = false) String cursor,

      @Parameter(description = "이전 페이지 마지막 요소 ID")
      @RequestParam(required = false) UUID idAfter,

      @Parameter(description = "정렬 필드 (targetDate, jobTime)")
      @RequestParam(defaultValue = "jobTime") String sortField,

      @Parameter(description = "정렬 방향 (asc, desc)")
      @RequestParam(defaultValue = "desc") String sortDirection,

      @Parameter(description = "페이지 크기")
      @RequestParam(defaultValue = "10") int size
  );
}