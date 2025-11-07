package com.ucamp.greenmap.badge.dto.request;

import lombok.Getter;

@Getter
public class BadgeRequest {
    private Long categoryId;
    private String name;
    private Long requirement;
    private String description;
    private String image_url;
}
