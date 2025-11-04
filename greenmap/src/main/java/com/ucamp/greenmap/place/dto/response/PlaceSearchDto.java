package com.ucamp.greenmap.place.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceSearchDto {
    private List<Long> placeIds;
}
