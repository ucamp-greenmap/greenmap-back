package com.ucamp.greenmap.place.service;

import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.place.domain.Place;
import com.ucamp.greenmap.place.dto.response.BikeResponse;
import com.ucamp.greenmap.place.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class BikeServiceImpl implements BikeService {

    private final WebClient bikeClient;
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;

    public BikeServiceImpl(@Qualifier("bikeWebClient") WebClient bikeClient,
                           PlaceRepository placeRepository,
                           CategoryRepository categoryRepository) {
        this.bikeClient = bikeClient;
        this.placeRepository = placeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Value("${seoul.bike-api-key}")
    private String apikey;

    @Override
    public String saveBikeStations() {
        // 따릉이 대여소 정보는 총 3200개, 1000개 단위로 나누어 fetch
        BikeResponse response1 = fetch(1, 1000);
        BikeResponse response2 = fetch(1001, 2000);
        BikeResponse response3 = fetch(2001, 3000);
        BikeResponse response4 = fetch(3001, 4000);
        List<BikeResponse> responses = List.of(response1, response2, response3, response4);

        // id 기준 중복 제거
        Map<String, BikeResponse.BaseInfo.BikeStation> bikeStationMap = new LinkedHashMap<>();
        for (BikeResponse response : responses) {
            for (BikeResponse.BaseInfo.BikeStation bs : response.getStationInfo().getRow()) {
                if (bs.getStationId() == null) continue;
                bikeStationMap.putIfAbsent(bs.getStationId(), bs);
            }
        }

        // DB에 저장
        Category category = categoryRepository.findByCategoryName(CategoryName.BIKE).orElseThrow(() -> new IllegalStateException("BIKE 카테고리가 DB에 없습니다."));
        for (BikeResponse.BaseInfo.BikeStation bs : bikeStationMap.values()) {
            String name = bs.getStationName();
            String address = bs.getAddress();
            double lat = Double.parseDouble(bs.getLatitude());
            double lng = Double.parseDouble(bs.getLongitude());
            Place place = placeRepository.findByPlaceNameAndAddress(name, address)
                    .map(ex -> {
                        boolean updated = false;
                        if (!Objects.equals(ex.getLocationX(), lng)) { ex.updateLocationX(lng); updated = true; }
                        if (!Objects.equals(ex.getLocationY(), lat)) { ex.updateLocationY(lat); updated = true; }
                        return ex;
                    })
                    .orElseGet(() -> placeRepository.save(
                            Place.builder()
                                    .placeName(name)
                                    .address(address)
                                    .category(category)  // 카테고리: 따릉이
                                    .locationX(lng)   // X=경도(longi)
                                    .locationY(lat)   // Y=위도(lat)
                                    .build()
                    ));
        }

        return "따릉이 대여소 정보 저장 완료";
    }

    private BikeResponse fetch(int start, int end) {
        return bikeClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{apikey}/json/tbCycleStationInfo/{start}/{end}/")
                        .build(apikey, start, end))
                .retrieve()
                .bodyToMono(BikeResponse.class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }
}
