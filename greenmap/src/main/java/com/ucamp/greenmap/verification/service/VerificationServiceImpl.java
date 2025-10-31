package com.ucamp.greenmap.verification.service;

import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.place.repository.PlaceRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.domain.PointHistory;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import com.ucamp.greenmap.verification.domain.Validate;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.verification.dto.response.MonthlyVerificationResponse;
import com.ucamp.greenmap.verification.dto.response.VerificationHistoryResponse;
import com.ucamp.greenmap.verification.repository.ValidateRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.place.domain.Place;
import com.ucamp.greenmap.verification.domain.History;
import com.ucamp.greenmap.verification.dto.request.BikeRequest;
import com.ucamp.greenmap.verification.dto.request.CarRequest;
import com.ucamp.greenmap.verification.dto.request.ShopRequest;
import com.ucamp.greenmap.verification.dto.response.VerificationResponse;
import com.ucamp.greenmap.verification.repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService{
    private final HistoryRepository historyRepository;
    private final ValidateRepository validateRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    public VerificationResponse verifyBike(Long memberId, BikeRequest bikeRequest) {
        // 인증 정보 생성
        String certInfo = bikeRequest.getBike_number() + bikeRequest.getStart_time() + bikeRequest.getEnd_time();

        // 중복 인증 검사
        if (validateRepository.existsByCertInfo(certInfo)) {
            throw new IllegalArgumentException("이미 인증된 자전거 이용 내역입니다.");
        }

        // 검증용 데이터 저장
        Validate validate = Validate.builder()
                .certInfo(certInfo)
                .build();
        validate.setCreatedAt(LocalDateTime.now());
        validateRepository.save(validate);



        // 필요한 엔티티 조회
        Place place = placeRepository.findById(1L).orElseThrow();
        Category category = categoryRepository.findByCategoryName(CategoryName.BIKE).orElseThrow();
        Member member = Member.builder()
                .memberId(memberId)
                .build();

        // Point, carbonSave 계산
        long carbonSave = (long) (Math.ceil((double) bikeRequest.getDistance() / 5));
        Long pointAmount = (long) ((double) bikeRequest.getDistance() / 0.1);

        // History 생성 및 저장
        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .distance(bikeRequest.getDistance())
                .carbonSave(carbonSave)
                .build();
        history.setCreatedAt(LocalDateTime.now());
        historyRepository.save(history);


        // Member의 Point, carbonSave 업데이트
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow();
        point.addPoint(pointAmount);
        point.addCarbonSaveTotal(carbonSave);

        // Response 반환
        return VerificationResponse.builder()
                .point(pointAmount)
                .carbonSave(history.getCarbonSave())
                .build();
    }

    @Override
    public VerificationResponse verifyCar(Long memberId, CarRequest carRequest) {
        // 인증 정보 생성
        String certInfo = carRequest.getChargeAmount() + carRequest.getStart_time() + carRequest.getEnd_time();

        // 중복 인증 검사
        if (validateRepository.existsByCertInfo(certInfo)) {
            throw new IllegalArgumentException("이미 인증된 전기차/수소차 충전 내역입니다.");
        }

        // 검증용 데이터 저장
        Validate validate = Validate.builder()
                .certInfo(certInfo)
                .build();
        validate.setCreatedAt(LocalDateTime.now());
        validateRepository.save(validate);

        // 필요한 엔티티 조회
        Category category = categoryRepository.findByCategoryName(CategoryName.CAR).orElseThrow();
        Place place = placeRepository.findById(3L).orElseThrow();
        Member member = Member.builder()
                .memberId(memberId)
                .build();

        // Point, carbonSave 계산
        long pointAmount = (long) ((double) carRequest.getChargeAmount() / 100);
        long carbonSave = (long) (Math.ceil((double) carRequest.getChargeAmount() / 7));

        // History 생성 및 저장
        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .chargeAmount(carRequest.getChargeAmount())
                .purchaseAmount(carRequest.getChargeFee())
                .carbonSave(carbonSave)
                .build();
        history.setCreatedAt(LocalDateTime.now());
        historyRepository.save(history);

        // Member의 Point, carbonSave 업데이트
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow();
        point.addPoint(pointAmount);
        point.addCarbonSaveTotal(carbonSave);

        // Response 반환
        return VerificationResponse.builder()
                .point(pointAmount)
                .carbonSave(carbonSave)
                .build();
    }

    @Override
    public VerificationResponse verifyShop(Long memberId, ShopRequest shopRequest) {
        // 인증 정보 생성
        String certInfo = shopRequest.getName() + shopRequest.getPrice() + shopRequest.getApproveNum();

        // 중복 인증 검사
        if (validateRepository.existsByCertInfo(certInfo)) {
            throw new IllegalArgumentException("이미 인증된 상점 이용 내역입니다.");
        }

        // 검증용 데이터 저장
        Validate validate = Validate.builder()
                .certInfo(certInfo)
                .build();
        validate.setCreatedAt(LocalDateTime.now());
        validateRepository.save(validate);

        // 필요한 엔티티 조회
        Place place = null;
        Category category = null;
        if (shopRequest.getCategory().equals("recycle")) {
            category = categoryRepository.findByCategoryName(CategoryName.RECYCLING_CENTER).orElseThrow();
            place = placeRepository.findById(4L).orElseThrow();
        } else if (shopRequest.getCategory().equals("zero")) {
            category = categoryRepository.findByCategoryName(CategoryName.ZERO_WASTE).orElseThrow();
            place = placeRepository.findById(2L).orElseThrow();
        } else {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
        Member member = Member.builder()
                .memberId(memberId)
                .build();

        // Point, carbonSave 계산
        long pointAmount = (long) ((double) shopRequest.getPrice() / 100);
        long carbonSave = (long) (Math.ceil((double) shopRequest.getPrice() / 20000));

        // History 생성 및 저장
        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .purchaseAmount(shopRequest.getPrice())
                .carbonSave(carbonSave)
                .build();
        history.setCreatedAt(LocalDateTime.now());
        historyRepository.save(history);

        // Member의 Point, carbonSave 업데이트
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow();
        point.addPoint(pointAmount);
        point.addCarbonSaveTotal(carbonSave);

        // Response 반환
        return VerificationResponse.builder()
                .point(pointAmount)
                .carbonSave(carbonSave)
                .build();
    }

    @Override
    public VerificationHistoryResponse getVerificationHistory(Long memberId) {
        List<History> historyList = historyRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
        List<VerificationHistoryResponse.VerificationHistoryItem> verificationHistoryItems = new ArrayList<>();
        for (History history : historyList) {
            PointHistory pointHistory = pointHistoryRepository.findByLogId(history.getHistoryId()).orElseThrow();
            verificationHistoryItems.add(VerificationHistoryResponse.VerificationHistoryItem.builder()
                    .category(history.getCategory().getCategoryName().toString())
                    .createdAt(history.getCreatedAt().toString())
                    .point(pointHistory.getPointAmount())
                    .build());
        }
        return VerificationHistoryResponse.builder()
                .historyItems(verificationHistoryItems)
                .build();
    }

    @Override
    public MonthlyVerificationResponse getMonthlyVerification(Long memberId) {
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow();
        return MonthlyVerificationResponse.builder()
                .verifyTimes(point.getPointTimes())
                .pointSum(point.getMonthPoint())
                .build();
    }
}
