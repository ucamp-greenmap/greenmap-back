package com.ucamp.greenmap.place.dto.response;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KepcoEvResponse {
    private List<EvStation> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvStation {
        private String csNm;   // 충전소명
        private String addr;   // 주소
        private String lat;    // 위도
        private String longi;  // 경도
    }
}
