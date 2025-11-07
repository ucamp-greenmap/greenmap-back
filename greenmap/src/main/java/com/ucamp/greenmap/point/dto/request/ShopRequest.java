package com.ucamp.greenmap.point.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShopRequest {
    private String imageUrl;
    private Long price;
    private String name;
    private String category;
    private String brand;
    private Boolean popular;
}
