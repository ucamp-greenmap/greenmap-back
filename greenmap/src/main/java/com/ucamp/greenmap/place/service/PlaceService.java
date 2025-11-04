package com.ucamp.greenmap.place.service;

import com.ucamp.greenmap.place.dto.response.PlaceDetailResponse;
import com.ucamp.greenmap.place.dto.response.PlaceListDto;

public interface PlaceService {
    PlaceDetailResponse getPlaceDetail(long memberId, Long placeId, Double longitude, Double latitude);

    PlaceListDto getAllPlaces(Long memberId, Double longitude, Double latitude);
}
