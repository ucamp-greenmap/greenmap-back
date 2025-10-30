package com.ucamp.greenmap.verification.dto.request;

import lombok.Getter;

@Getter
public class ShopRequest {
    private String category;
    private String name;
    private Long price;
    private Long approveNum;
}
