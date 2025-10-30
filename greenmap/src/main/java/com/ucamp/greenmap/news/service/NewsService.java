package com.ucamp.greenmap.news.service;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.news.dto.request.NewsRequest;
import com.ucamp.greenmap.news.dto.response.NewsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface NewsService {
    public ResponseEntity<ApiResponse<NewsResponse>> searchNews();

    ResponseEntity<ApiResponse<String>> viewNews(NewsRequest newsRequest);
}
