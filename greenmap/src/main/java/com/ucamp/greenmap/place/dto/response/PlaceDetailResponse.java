package com.ucamp.greenmap.place.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceDetailResponse {
    private String placeName;
    private String address;
    private Double distance;
    private String openingHours;
    private String telNum;
    private String imageUrl;
    private Boolean isBookmarked;
}
