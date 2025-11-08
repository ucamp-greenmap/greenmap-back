package com.ucamp.greenmap.badge.dto.request;

import lombok.Getter;

@Getter
public class BadgeRequest {
    private String name;
    private Long requirement;
    private String description; // 공백으로 split해서 첫번째 요소를 카테고리로 사용
    private String image_url;
}
