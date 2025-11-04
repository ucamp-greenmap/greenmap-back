package com.ucamp.greenmap.place.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceDto {
    private Long placeId;
    private String placeName;
    private String address;
    private Double distance;
    private String openingHours;
    private String telNum;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private Boolean isBookMarked;
    private Long categoryId;
}
