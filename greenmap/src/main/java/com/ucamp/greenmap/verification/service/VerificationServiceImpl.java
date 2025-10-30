package com.ucamp.greenmap.verification.service;

import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.place.repository.PlaceRepository;
import com.ucamp.greenmap.verification.domain.Validate;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.verification.repository.ValidateRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.place.domain.Place;
import com.ucamp.greenmap.verification.domain.History;
import com.ucamp.greenmap.verification.dto.request.BikeRequest;
import com.ucamp.greenmap.verification.dto.request.CarRequest;
import com.ucamp.greenmap.verification.dto.request.ShopRequest;
import com.ucamp.greenmap.verification.dto.response.VerificationResponse;
import com.ucamp.greenmap.verification.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService{
    private final HistoryRepository historyRepository;
    private final ValidateRepository validateRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;

    @Override
    public VerificationResponse verifyBike(Long memberId, BikeRequest bikeRequest) {
        validateRepository.save(Validate.builder()
                .certInfo(bikeRequest.getBikeNumber() + bikeRequest.getStart_time() + bikeRequest.getEnd_time())
                .build());
        Place place = placeRepository.findById(1L).get();
        Category category = categoryRepository.findByCategoryName(CategoryName.BIKE).get();

        Member member = Member.builder()
                .memberId(memberId)
                .build();

        log.info("in Verification serviceImpl, category = {}", category.getCategoryName());

        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .distance(bikeRequest.getDistance())
                .carbonSave((long) (Math.ceil((double) bikeRequest.getDistance() / 5)))
                .build();

        historyRepository.save(history);

        Long point = (long) ((double) bikeRequest.getDistance() / 0.1);

        log.info("in Verification serviceImpl, point = {}", point);

        return VerificationResponse.builder()
                .point(point)
                .carbonSave(history.getCarbonSave())
                .build();
    }

    @Override
    public VerificationResponse verifyCar(Long memberId, CarRequest carRequest) {
        return VerificationResponse.builder()
                .point(1L)
                .carbonSave(3L)
                .build();
    }

    @Override
    public VerificationResponse verifyShop(Long memberId, ShopRequest shopRequest) {
        return VerificationResponse.builder()
                .point(1L)
                .carbonSave(3L)
                .build();
    }
}
