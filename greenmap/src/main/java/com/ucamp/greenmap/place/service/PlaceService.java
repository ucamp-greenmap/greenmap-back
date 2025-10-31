package com.ucamp.greenmap.place.service;

import com.ucamp.greenmap.place.dto.response.PlaceDetailResponse;

public interface PlaceService {
    PlaceDetailResponse getPlaceDetail(long memberId, Long placeId, Double longitude, Double latitude);
}
