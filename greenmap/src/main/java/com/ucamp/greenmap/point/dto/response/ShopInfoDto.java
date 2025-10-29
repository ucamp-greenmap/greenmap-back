package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopInfoDto {
    private Long point;
    private List<VoucherDto> voucherList;

    public static ShopInfoDto of(Long point, List<VoucherDto> vouchers) {
        return ShopInfoDto.builder()
                .point(point)
                .voucherList(vouchers)
                .build();
    }
}
