package com.ucamp.greenmap.verification.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryCarbonDto {
    private Long historyId;
    private Long carbonSave;
}
