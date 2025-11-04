package com.ucamp.greenmap.place.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.place.domain.Place;
import com.ucamp.greenmap.place.dto.response.KepcoEvResponse;
import com.ucamp.greenmap.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class KepcoEvIngestService {

    private final WebClient kepcoClient;
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;

    public KepcoEvIngestService(@Qualifier("kepcoClient") WebClient kepcoClient, PlaceRepository placeRepository, CategoryRepository categoryRepository) {
        this.kepcoClient = kepcoClient;
        this.placeRepository = placeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Value("${kepco.path}")   private String path;
    @Value("${kepco.api-key}") private String apiKey;

    public int syncByAddress(String addrParam) {
        List<KepcoEvResponse.EvStation> items = fetch(addrParam);
        if (items == null || items.isEmpty()) return 0;

        Category carCategory = categoryRepository.findByCategoryName(CategoryName.EVCAR)
                .orElseThrow(() -> new IllegalStateException("CAR 카테고리가 DB에 없습니다."));

        // 1) csNm|addr 기준으로 중복 제거
        Map<String, KepcoEvResponse.EvStation> unique = new LinkedHashMap<>();
        for (KepcoEvResponse.EvStation it : items) {
            if (it.getCsNm() == null || it.getAddr() == null) continue;
            unique.putIfAbsent(it.getCsNm() + "|" + it.getAddr(), it);
        }

        // 2) update + insert
        int changed = 0;
        for (KepcoEvResponse.EvStation ev : unique.values()) {
            String name = ev.getCsNm();
            String address = ev.getAddr();
            double lat = parseDouble(ev.getLat());
            double lng = parseDouble(ev.getLongi());

            Place place = placeRepository.findByPlaceNameAndAddress(name, address)
                    .map(ex -> {
                        boolean updated = false;
                        if (!Objects.equals(ex.getLocationX(), lng)) { ex.updateLocationX(lng); updated = true; }
                        if (!Objects.equals(ex.getLocationY(), lat)) { ex.updateLocationY(lat); updated = true; }
                        if (updated) placeRepository.save(ex);
                        return ex;
                    })
                    .orElseGet(() -> placeRepository.save(
                            Place.builder()
                                    .placeName(name)
                                    .address(address)
                                    .detailAddress(null)
                                    .category(carCategory)  // 카테고리: 전기차충전소
                                    .telNum(null)
                                    .locationX(lng)   // X=경도(longi)
                                    .locationY(lat)   // Y=위도(lat)
                                    .image(null)
                                    .build()
                    ));
            changed++;
        }
        return changed;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<KepcoEvResponse.EvStation> fetch(String addr) {
        String body = kepcoClient.get()
                .uri(uri -> uri.path(path)
                        .queryParam("apiKey", apiKey)
                        .queryParam("addr", addr)
                        .queryParam("type", "json")
                        .build())
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (body == null || body.isBlank()) return List.of();

        String trimmed = body.stripLeading();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                KepcoEvResponse resp = objectMapper
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(body, KepcoEvResponse.class);
                return resp.getData() != null ? resp.getData() : List.of();
            } catch (Exception e) {
                System.err.println("JSON 파싱 실패: " + e.getMessage());
                return List.of();
            }
        } else {
            return List.of();
        }
    }

    private double parseDouble(String s) {
        if (s == null || s.isBlank()) return 0.0;
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

}