package com.ucamp.greenmap.news.service;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.news.dto.response.NewsResponse;
import org.springframework.http.ResponseEntity;

public interface NewsService {
    public ResponseEntity<ApiResponse<NewsResponse>> searchNews();
}
