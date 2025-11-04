package com.ucamp.greenmap.place.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceListDto {
    private Long count;
    List<PlaceDto> places;
}
