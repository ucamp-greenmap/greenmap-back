package com.ucamp.greenmap.news.service;

import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.news.domain.NewsViewLog;
import com.ucamp.greenmap.news.dto.request.NewsRequest;
import com.ucamp.greenmap.news.dto.response.NewsResponse;
import com.ucamp.greenmap.news.repository.NewsRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.domain.PointHistory;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NewsServiceImpl implements NewsService {
    private final WebClient webClient;
    private final NewsRepository newsRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Autowired
    public NewsServiceImpl(@Qualifier("naverWebClient") WebClient webClient,
                           NewsRepository newsRepository,
                           PointRepository pointRepository,
                           PointHistoryRepository pointHistoryRepository) {
        this.webClient = webClient;
        this.newsRepository = newsRepository;
        this.pointRepository = pointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    /**
     * 네이버 뉴스 검색
     *
     * @param query   검색어 (예: "친환경")
     * @param display 검색 결과 개수 (기본값: 10, 최대: 100)
     * @param start   검색 시작 위치 (기본값: 1, 최대: 1000)
     * @param sort    정렬 옵션 (sim: 정확도순, date: 날짜순)
     * @return 뉴스 검색 결과
     */
    @Override
    public ResponseEntity<ApiResponse<NewsResponse>> searchNews() {

        try {
            NewsResponse newsResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search/news.json")
                            .queryParam("query", "환경")
                            .queryParam("display", 6)
                            .build())
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        log.info("Client error : {}", response.statusCode());
                        return Mono.error(new RuntimeException("Client Error가 발생했습니다."));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, response -> {
                        log.info("Server error : {}", response.statusCode());
                        return Mono.error(new RuntimeException("Server Error가 발생했습니다."));
                    })
                    .bodyToMono(NewsResponse.class)
                    .block();


            List<NewsResponse.NewsItem> newsList = null;
            try {
                newsList = newsResponse.getItems();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (newsList.size() > 4) {
                newsList.removeLast();
            }

            for (NewsResponse.NewsItem item : newsList) {
                if (newsRepository.existsByNewsTitleAndMember_MemberId(item.getTitle(), 1L)) {
                    item.setRead(true);
                }
            }

            return ResponseEntity.ok(ApiResponse.success("성공적으로 뉴스 목록을 조회했습니다.", newsResponse));

        } catch (RuntimeException e) {
            log.info("error catched : " + e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("실패"));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<String>> viewNews(NewsRequest request) {

        try {
            Member member = Member.builder()
                    .memberId(1L)
                    .image(new Image(1L, "url"))
                    .email("email")
                    .nickname("nickname")
                    .password("password")
                    .build();
            Category category = new Category(1L, CategoryName.NEWS);
            NewsViewLog log = NewsViewLog.builder()
                    .member(member)
                    .newsTitle(request.getTitle())
                    .build();
            log.setCreatedAt(LocalDateTime.now());

            newsRepository.save(log);

            NewsViewLog newsViewLog = newsRepository.findByNewsTitleAndMember_MemberId(request.getTitle(), member.getMemberId());

            PointHistory pointHistory = PointHistory.builder()
                    .member(member)
                    .category(category)
                    .pointAmount(5L)
                    .description("description")
                    .logId(newsViewLog.getLogId())
                    .build();

            pointHistory.setCreatedAt(LocalDateTime.now());

            pointHistoryRepository.save(pointHistory);

            Point point = pointRepository.findByMember_MemberId(member.getMemberId()).orElseThrow();
            point.addPoint(5L);

            return ResponseEntity.ok(ApiResponse.success("성공적으로 뉴스를 조회했습니다."));
        } catch (Exception e) {
            log.error("error catched : " + e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("실패"));
        }
    }

    private String removeHtmlTags(String text) {
        return text.replaceAll("<[^>]*>", "")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }

}
