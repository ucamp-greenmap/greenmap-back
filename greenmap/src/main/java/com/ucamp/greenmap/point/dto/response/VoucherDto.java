package com.ucamp.greenmap.point.dto.response;

import com.ucamp.greenmap.point.domain.Voucher;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoucherDto {
    private Long voucherId;
    private String imageUrl;
    private String name;
    private Long price;

    public static VoucherDto fromEntity(Voucher voucher) {
        return VoucherDto.builder()
                .voucherId(voucher.getVoucherId())
                .imageUrl(voucher.getImage().getImageUrl())
                .name(voucher.getName())
                .price(voucher.getPrice())
                .build();
    }
}
