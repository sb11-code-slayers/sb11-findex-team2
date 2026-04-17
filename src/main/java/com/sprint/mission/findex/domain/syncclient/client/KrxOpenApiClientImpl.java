package com.sprint.mission.findex.domain.syncclient.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.findex.domain.syncclient.dto.IndexDataApiResponse;
import com.sprint.mission.findex.domain.syncclient.dto.KrxApiResponseWrapper;
import com.sprint.mission.findex.global.exception.ApiException;
import com.sprint.mission.findex.global.exception.ApiException.ERROR;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

@Component
public class KrxOpenApiClientImpl implements KrxOpenApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;

    public KrxOpenApiClientImpl(
            @Value("${public-data.base-url}") String baseUrl,
            @Value("${public-data.api-key}") String apiKey,
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<IndexDataApiResponse> fetchByDateRange(String indexName, LocalDate from, LocalDate to) {
        if (indexName == null || indexName.isBlank()) {
            throw new ApiException(ERROR.COMMON_INVALID_REQUEST);
        }
        if (from == null || to == null) {
            throw new ApiException(ERROR.COMMON_INVALID_REQUEST);
        }
        if (from.isAfter(to)) {
            throw new ApiException(ERROR.COMMON_INVALID_REQUEST);
        }

        try {
            String encodedIndexName = UriUtils.encode(indexName, StandardCharsets.UTF_8);
            int pageNo = 1;
            int numOfRows = 1000;
            List<IndexDataApiResponse> allItems = new ArrayList<>();

            while (true) {
                String url = baseUrl + "/getStockMarketIndex"
                        + "?serviceKey=" + apiKey
                        + "&resultType=json"
                        + "&pageNo=" + pageNo
                        + "&numOfRows=" + numOfRows
                        + "&beginBasDt=" + from.format(DateTimeFormatter.BASIC_ISO_DATE)
                        + "&endBasDt=" + to.format(DateTimeFormatter.BASIC_ISO_DATE)
                        + "&idxNm=" + encodedIndexName;

                URI uri = URI.create(url);
                String responseBody = restTemplate.getForObject(uri, String.class);

                if (responseBody == null || responseBody.isBlank()) {
                    break;
                }

                KrxApiResponseWrapper wrapper =
                        objectMapper.readValue(responseBody, KrxApiResponseWrapper.class);

                if (wrapper.response() == null
                        || wrapper.response().body() == null
                        || wrapper.response().body().items() == null) {
                    break;
                }

                List<IndexDataApiResponse> pageItems =
                        wrapper.response().body().items().item();

                if (pageItems == null || pageItems.isEmpty()) {
                    break;
                }

                allItems.addAll(pageItems);

                if (pageItems.size() < numOfRows) {
                    break;
                }

                pageNo++;
            }

            return allItems.isEmpty() ? Collections.emptyList() : allItems;

        } catch (ApiException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ApiException(ERROR.SYNC_JOB_OPEN_API_ERROR);
        } catch (Exception e) {
            throw new ApiException(ERROR.SYNC_JOB_OPEN_API_ERROR);
        }
    }

    @Override
    public List<IndexDataApiResponse> fetchByDate(LocalDate date) {
        if (date == null) {
            throw new ApiException(ERROR.COMMON_INVALID_REQUEST);
        }
        if (date.isAfter(LocalDate.now())) {
            throw new ApiException(ERROR.COMMON_INVALID_REQUEST);
        }

        try {
            int pageNo = 1;
            int numOfRows = 1000;
            List<IndexDataApiResponse> allItems = new ArrayList<>();

            while (true) {
                String url = baseUrl + "/getStockMarketIndex"
                        + "?serviceKey=" + apiKey
                        + "&resultType=json"
                        + "&pageNo=" + pageNo
                        + "&numOfRows=" + numOfRows
                        + "&beginBasDt=" + date.format(DateTimeFormatter.BASIC_ISO_DATE)
                        + "&endBasDt=" + date.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

                URI uri = URI.create(url);
                String responseBody = restTemplate.getForObject(uri, String.class);

                if (responseBody == null || responseBody.isBlank()) {
                    break;
                }

                KrxApiResponseWrapper wrapper =
                        objectMapper.readValue(responseBody, KrxApiResponseWrapper.class);

                if (wrapper.response() == null
                        || wrapper.response().body() == null
                        || wrapper.response().body().items() == null) {
                    break;
                }

                List<IndexDataApiResponse> pageItems =
                        wrapper.response().body().items().item();

                if (pageItems == null || pageItems.isEmpty()) {
                    break;
                }

                allItems.addAll(pageItems);

                if (pageItems.size() < numOfRows) {
                    break;
                }

                pageNo++;
            }

            return allItems.isEmpty() ? Collections.emptyList() : allItems;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ERROR.SYNC_JOB_OPEN_API_ERROR);
        }
    }
}