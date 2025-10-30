package com.ucamp.greenmap.news.controller;
import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.news.dto.request.NewsRequest;
import com.ucamp.greenmap.news.dto.response.NewsResponse;
import com.ucamp.greenmap.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<ApiResponse<NewsResponse>> searchNews() {
        return newsService.searchNews();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> viewNews(@RequestBody NewsRequest newsRequest) {
        return newsService.viewNews(newsRequest);
    }
}