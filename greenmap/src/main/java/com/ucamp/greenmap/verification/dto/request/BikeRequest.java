package com.ucamp.greenmap.verification.dto.request;

import lombok.Getter;

@Getter
public class BikeRequest {
    private String category;
    private Long bike_number;
    private Double distance;
    private String start_time;
    private String end_time;
}
