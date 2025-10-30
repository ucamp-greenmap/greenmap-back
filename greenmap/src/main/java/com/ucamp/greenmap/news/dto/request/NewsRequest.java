package com.ucamp.greenmap.news.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {
    private Long memberId;
    private String title;
}
