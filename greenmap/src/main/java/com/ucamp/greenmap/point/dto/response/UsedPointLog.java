package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UsedPointLog {
    private String description;
    private Long pointAmount;
    private LocalDateTime date;
    private String category;
}
