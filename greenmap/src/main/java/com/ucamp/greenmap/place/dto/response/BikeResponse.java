package com.ucamp.greenmap.place.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BikeResponse {
    @JsonProperty("stationInfo")
    private BaseInfo stationInfo;

    @Getter
    @Builder
    public static class BaseInfo {
        @JsonProperty("list_total_count")
        private Long totalCount;
        private List<BikeStation> row;

        @Getter
        @Builder
        public static class BikeStation {
            @JsonProperty("RENT_ID")
            private String stationId;
            @JsonProperty("RENT_NM")
            private String stationName;
            @JsonProperty("STA_LAT")
            private String latitude;
            @JsonProperty("STA_LONG")
            private String longitude;
            @JsonProperty("STA_ADD1")
            private String address;

            @Override
            public String toString() {
                return "BikeStation{" +
                        "stationName='" + stationName + '\'' +
                        ", latitude='" + latitude + '\'' +
                        ", longitude='" + longitude + '\'' +
                        ", address='" + address + '\'' +
                        '}';
            }
        }
    }
}

