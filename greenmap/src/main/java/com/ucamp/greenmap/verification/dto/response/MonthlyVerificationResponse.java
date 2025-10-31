package com.ucamp.greenmap.verification.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyVerificationResponse {
    private Long verifyTimes;
    private Long pointSum;
}
