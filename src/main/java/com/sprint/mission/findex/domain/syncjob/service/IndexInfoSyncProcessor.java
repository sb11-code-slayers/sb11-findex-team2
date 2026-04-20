package com.sprint.mission.findex.domain.syncjob.service;

import com.sprint.mission.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.sprint.mission.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.sprint.mission.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.mission.findex.domain.indexinfo.service.IndexInfoService;
import com.sprint.mission.findex.domain.syncjob.dto.SyncJobResponse;
import com.sprint.mission.findex.domain.syncjob.entity.JobResult;
import com.sprint.mission.findex.domain.syncjob.entity.JobType;
import com.sprint.mission.findex.domain.syncjob.entity.SyncJob;
import com.sprint.mission.findex.domain.syncjob.repository.SyncJobRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class IndexInfoSyncProcessor {

    private final IndexInfoService indexInfoService;
    private final SyncJobRepository syncJobRepository;

    @Transactional
    public SyncJobResponse createAndSaveHistory(IndexInfoCreateRequest createRequest, LocalDate targetDate, String workerIp) {
        IndexInfo created = indexInfoService.createByOpenAPI(createRequest);
        return saveHistory(created, targetDate, workerIp, JobResult.SUCCESS, null);
    }

    @Transactional
    public SyncJobResponse updateAndSaveHistory(IndexInfo indexInfo, IndexInfoUpdateRequest updateRequest, LocalDate targetDate, String workerIp) {
        indexInfoService.updateByOpenAPI(indexInfo, updateRequest);
        return saveHistory(indexInfo, targetDate, workerIp, JobResult.SUCCESS, null);
    }

    private SyncJobResponse saveHistory(IndexInfo indexInfo, LocalDate targetDate, String workerIp, JobResult result, String errorMessage) {
        SyncJob syncJob = SyncJob.builder()
            .indexInfo(indexInfo)
            .jobType(JobType.INDEX_INFO)
            .targetDate(targetDate)
            .worker(workerIp)
            .result(result)
            .errorMessage(errorMessage)
            .build();
        return SyncJobResponse.from(syncJobRepository.save(syncJob));
    }
}