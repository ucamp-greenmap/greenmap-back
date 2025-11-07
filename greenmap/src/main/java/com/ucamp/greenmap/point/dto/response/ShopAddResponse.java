package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopAddResponse {
    private Long voucherId;
    private String imageUrl;
    private Long price;
    private String name;
    private String category;
    private String brand;
    private Boolean popular;
}
