package com.ucamp.greenmap.point.dto.request;

import com.ucamp.greenmap.common.domain.CategoryName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsePointRequest {

    private Long point;
    private CategoryName type; // "VOUCHER" or "CASH"
}
