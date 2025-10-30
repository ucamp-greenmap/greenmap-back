package com.ucamp.greenmap.verification.dto.response;

import lombok.*;

@Data
@Builder
public class VerificationResponse {
    private Long point;
    private Long carbonSave;
}
