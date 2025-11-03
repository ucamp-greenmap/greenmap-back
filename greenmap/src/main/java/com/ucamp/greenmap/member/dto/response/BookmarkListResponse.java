package com.ucamp.greenmap.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkListResponse {
    private Long placeId;
    private String placeName;
    private String address;
    private String detailAddress;
    private Double locationX;
    private Double locationY;
    private String telNum;

    private String weekdayOpen;
    private String weekdayClose;
    private String weekendOpen;
    private String weekendClose;
    private String openingDays;  // 운영 요일 정보
}
