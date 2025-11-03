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
import java.time.LocalDateTime;
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
        log.info("여기까진 왔겠지 설마");
        // 따릉이 대여소 정보는 총 3200개, 1000개 단위로 나누어 fetch
        BikeResponse response1 = fetch(1, 10);
//        BikeResponse response1 = fetch(1, 1000);
//        log.info("fetch 1개");
//        BikeResponse response2 = fetch(1001, 2000);
//        log.info("fetch 1개");
//        BikeResponse response3 = fetch(2001, 3000);
//        log.info("fetch 1개");
//        BikeResponse response4 = fetch(3001, 4000);
//        log.info("fetch 1개");
//        List<BikeResponse> responses = List.of(response1, response2, response3, response4);
        List<BikeResponse> responses = List.of(response1);

        log.info("따릉이 대여소 정보 fetch 완료");

        // id 기준 중복 제거
        Map<String, BikeResponse.BaseInfo.BikeStation> bikeStationMap = new LinkedHashMap<>();
        for (BikeResponse response : responses) {
            for (BikeResponse.BaseInfo.BikeStation bs : response.getStationInfo().getRow()) {
                if (bs.getStationId() == null) continue;
                bikeStationMap.putIfAbsent(bs.getStationId(), bs);
            }
        }

        log.info("중복 제거 완료");

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
        log.info("따릉이 대여소 정보 DB 저장 완료");

        return "따릉이 대여소 정보 저장 완료";
    }

    private BikeResponse fetch(int start, int end) {
        LocalDateTime startTime = LocalDateTime.now();
        BikeResponse response = bikeClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{apikey}/json/tbCycleStationInfo/{start}/{end}/")
                        .build(apikey, start, end))
                .retrieve()
                .bodyToMono(BikeResponse.class)
//                .timeout(Duration.ofSeconds(20))
                .block();
        LocalDateTime endTime = LocalDateTime.now();
        log.info("따릉이 대여소 정보 fetch 완료 (start: {}, end: {}, 소요시간: {}초)", start, end, Duration.between(startTime, endTime).toSeconds());
        return response;
    }
}
