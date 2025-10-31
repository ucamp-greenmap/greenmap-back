package com.ucamp.greenmap.image.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {
    private Long imageId;
    private String imageUrl;
}
