package com.ucamp.greenmap.news.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.news.dto.response.NewsResponse;
import com.ucamp.greenmap.news.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    private ResponseEntity<ApiResponse<NewsResponse>> searchNews() {
        return newsService.searchNews();
    }
}