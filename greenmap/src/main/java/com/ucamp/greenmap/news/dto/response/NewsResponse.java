package com.ucamp.greenmap.news.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewsResponse {
    private String lastBuildDate;
    private List<NewsItem> items;

    @Data
    public static class NewsItem {
        private String title;
        @JsonProperty("originallink")
        private String originalLink;
        private String link;
        private String description;
        private String pubDate;
        @JsonIgnore
        private boolean isRead = false;
    }
}
