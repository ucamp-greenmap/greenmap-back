package com.ucamp.greenmap.news.service;

import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.news.domain.NewsViewLog;
import com.ucamp.greenmap.news.dto.request.NewsRequest;
import com.ucamp.greenmap.news.dto.response.NewsResponse;
import com.ucamp.greenmap.news.repository.NewsRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.domain.PointHistory;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class NewsServiceImpl implements NewsService {
    private final WebClient webClient;
    private final NewsRepository newsRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final CategoryRepository categoryRepository;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Autowired
    public NewsServiceImpl(@Qualifier("naverWebClient") WebClient webClient,
                           NewsRepository newsRepository,
                           PointRepository pointRepository,
                           PointHistoryRepository pointHistoryRepository,
                           MemberBadgeRepository memberBadgeRepository,
                           BadgeRepository badgeRepository,
                           CategoryRepository categoryRepository) {
        this.webClient = webClient;
        this.newsRepository = newsRepository;
        this.pointRepository = pointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.memberBadgeRepository = memberBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 네이버 뉴스 검색
     * <p>
     * // @param query   검색어 (예: "친환경")
     * // @param display 검색 결과 개수 (기본값: 10, 최대: 100)
     * // @param start   검색 시작 위치 (기본값: 1, 최대: 1000)
     * // @param sort    정렬 옵션 (sim: 정확도순, date: 날짜순)
     *
     * @return 뉴스 검색 결과
     */
    @Override
    public NewsResponse searchNews() {

        // WebClient를 사용하여 네이버 뉴스 검색 API 호출
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
                .timeout(Duration.ofSeconds(10))
                .block();

        // API 응답 검증
        if (newsResponse == null) {
            throw new RuntimeException("뉴스 검색 API 응답이 null입니다.");
        }

        // 받아온 뉴스들 중 4개만 남기기
        List<NewsResponse.NewsItem> newsList = newsResponse.getItems();
        while (newsList.size() > 4) {
            newsList.removeLast();
        }

        // 이미 읽은 뉴스인지 확인 및 isRead 설정
        for (NewsResponse.NewsItem item : newsList) {
            if (newsRepository.existsByNewsTitleAndMember_MemberId(item.getTitle(), 1L)) {
                item.setRead(true);
            }
        }

        // 성공 응답 반환
        return newsResponse;

    }

    @Override
    public String viewNews(NewsRequest request) {

        // 필요한 엔티티 생성 및 조회
        Member member = Member.builder()
                .memberId(request.getMemberId())
                .build();
        Category category = categoryRepository.findByCategoryName(CategoryName.NEWS).orElseThrow();

        // 뉴스 뷰 로그 저장
        NewsViewLog log = NewsViewLog.builder()
                .member(member)
                .newsTitle(request.getTitle())
                .build();
        log.setCreatedAt(LocalDateTime.now());
        newsRepository.save(log);

        // 포인트 히스토리 저장
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

        // 포인트 적립
        Point point = pointRepository.findByMember_MemberId(member.getMemberId()).orElseThrow();
        point.addPoint(5L);

        // 뱃지 최신화
        MemberBadge memberBadge = memberBadgeRepository.findByMember_MemberId(member.getMemberId()).orElseThrow();
        Badge nextBadge = badgeRepository.findById(
                memberBadge.getBadge().getBadgeId() != 5 ?
                        memberBadge.getBadge().getBadgeId() + 1 : 5
        ).orElseThrow();
        if (point.getWholePoint() >= nextBadge.getRequirement() && memberBadge.getBadge().getBadgeId() != 5) {
            memberBadge.updateBadge(nextBadge);
        }

        // 성공 응답 반환
        return "성공적으로 뉴스를 조회했습니다.";
    }

    // HTML 태그 제거 메서드 (현재 미사용)
    private String removeHtmlTags(String text) {
        return text.replaceAll("<[^>]*>", "")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }

}
