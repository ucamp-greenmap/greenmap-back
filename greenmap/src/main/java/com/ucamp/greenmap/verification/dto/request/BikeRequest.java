package com.ucamp.greenmap.verification.dto.request;

import lombok.Getter;

@Getter
public class BikeRequest {
    private String category;
    private Long bikeNumber;
    private Long distance;
    private String start_time;
    private String end_time;
}
