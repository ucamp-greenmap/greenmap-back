package com.ucamp.greenmap.place.service;

import com.ucamp.greenmap.member.repository.BookmarkRepository;
import com.ucamp.greenmap.place.domain.OpeningHours;
import com.ucamp.greenmap.place.domain.Place;
import com.ucamp.greenmap.place.dto.response.PlaceDetailResponse;
import com.ucamp.greenmap.place.dto.response.PlaceDto;
import com.ucamp.greenmap.place.dto.response.PlaceListDto;
import com.ucamp.greenmap.place.dto.response.PlaceSearchDto;
import com.ucamp.greenmap.place.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public PlaceDetailResponse getPlaceDetail(long memberId, Long placeId, Double longitude, Double latitude) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));

        // 1) 좌표 파싱 (locationX=경도, locationY=위도 라고 가정)
        double placeLat = place.getLocationY();
        double placeLon = place.getLocationX();

        // 2) 거리 계산 (km)
        double distanceKm = haversineKm(latitude, longitude, placeLat, placeLon);
        double distanceRounded = Math.round(distanceKm * 10.0) / 10.0; // 소수 1자리

        // 3) 운영시간 문자열
        String openingHours = resolveOpeningHours(place.getOpeningHours());

        // 4) 북마크 여부
        boolean isBookmarked = bookmarkRepository.existsByMember_MemberIdAndPlace_PlaceId(memberId, placeId);

        return PlaceDetailResponse.builder()
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .distance(distanceRounded)
                .openingHours(openingHours)
                .telNum(place.getTelNum())
                .imageUrl(place.getImage().getImageUrl())
                .isBookmarked(isBookmarked)
                .build();
    }

    @Override
    @Transactional
    public PlaceListDto getAllPlaces(Long memberId, Double longitude, Double latitude) {
        // 1) 북마크 된 placeId들을 한 번에
        Set<Long> bookmarked = (memberId == null)
                ? Set.of()
                : new HashSet<>(bookmarkRepository.findPlaceIdsByMemberId(memberId));

        // 2) 장소도 한 번에 (fetch join 버전 권장)
        List<Place> placesEntity = placeRepository.findAllWithJoins(); // 또는 findAll()

        // 3) 매핑
        List<PlaceDto> places = placesEntity.stream().map(p -> {
            double distanceKm = haversineKm(latitude, longitude, p.getLocationY(), p.getLocationX());
            double distanceRounded = Math.round(distanceKm * 10.0) / 10.0;

            String imageUrl = (p.getImage() != null) ? p.getImage().getImageUrl() : null;

            return PlaceDto.builder()
                    .placeId(p.getPlaceId())
                    .placeName(p.getPlaceName())
                    .address(p.getAddress())
                    .distance(distanceRounded)
                    .openingHours(resolveOpeningHours(p.getOpeningHours()))
                    .telNum(p.getTelNum())
                    .categoryId(p.getCategory().getCategoryId())
                    .latitude(p.getLocationY())
                    .longitude(p.getLocationX())
                    .imageUrl(imageUrl)
                    .isBookMarked(bookmarked.contains(p.getPlaceId()))
                    .build();
        }).toList();
        return PlaceListDto.builder()
                .count((long) places.size())
                .places(places)
                .build();
    }


    @Override
    public PlaceSearchDto searchPlaces(String search) {
        List<Place> places = placeRepository.findByPlaceNameContainingOrAddressContaining(search, search);

        return PlaceSearchDto.builder()
                .placeIds(places.stream().map(Place::getPlaceId).toList())
                .build();
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) *
                                Math.cos(Math.toRadians(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String resolveOpeningHours(OpeningHours oh) {
        if (oh == null) return "정보 없음";

        DayOfWeek today = LocalDate.now(ZoneId.of("Asia/Seoul")).getDayOfWeek();
        boolean isWeekend = (today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY);

        String open  = isWeekend ? oh.getWeekendOpen()  : oh.getWeekdayOpen();
        String close = isWeekend ? oh.getWeekendClose() : oh.getWeekdayClose();

        if (isBlank(open) || isBlank(close)) {
            return "휴무";
        }

        return open + " ~ " + close;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}

