package com.ucamp.greenmap.verification.dto.request;

import lombok.Getter;

@Getter
public class CarRequest {
    private String category;
    private Long chargeAmount;
    private String start_time;
    private String end_time;
}
