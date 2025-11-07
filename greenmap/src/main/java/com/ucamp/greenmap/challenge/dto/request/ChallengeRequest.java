package com.ucamp.greenmap.challenge.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRequest {
    private LocalDateTime updatedAt;
    private String challengeName;
    private String description;
    private Long memberCount;
    private Long deadline;
    private Long success;
    private Long pointAmount;

}
