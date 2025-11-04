package com.ucamp.greenmap.news.service;

import com.ucamp.greenmap.news.dto.request.NewsRequest;
import com.ucamp.greenmap.news.dto.response.NewsResponse;

public interface NewsService {
    NewsResponse searchNews(Long memberId);

    String viewNews(Long memberId, NewsRequest newsRequest);
}
