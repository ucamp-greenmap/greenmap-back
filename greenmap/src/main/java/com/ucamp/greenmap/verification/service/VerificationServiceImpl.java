package com.ucamp.greenmap.verification.service;

import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
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

import java.util.ArrayList;
import java.util.List;

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
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;

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
        validate.setCreatedAt();
        validateRepository.save(validate);

        // 필요한 엔티티 조회
        Category category = categoryRepository.findByCategoryName(CategoryName.BIKE).orElseThrow(
                () -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        Place place = placeRepository.findByCategory_CategoryId(category.getCategoryId()).orElseThrow(
                () -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        Member member = Member.builder()
                .memberId(memberId)
                .build();

        // Point, carbonSave 계산
        Long carbonSave = (long) (Math.ceil((double) bikeRequest.getDistance() / 5));
        Long pointAmount = (long) ((double) bikeRequest.getDistance() / 0.1);

        // 인증 내역 생성 및 저장
        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .distance(bikeRequest.getDistance())
                .carbonSave(carbonSave)
                .build();
        history.setCreatedAt();
        historyRepository.save(history);

        // PointHistory 생성 및 저장
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .category(category)
                .pointAmount(pointAmount)
                .description("따릉이 이용 인증")
                .logId(history.getHistoryId())
                .build();
        pointHistory.setCreatedAt();
        pointHistoryRepository.save(pointHistory);

        // Member의 Point, carbonSave 업데이트
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow(
                () -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));
        point.addPoint(pointAmount);
        point.addCarbonSaveTotal(carbonSave);

        // 뱃지 최신화
        MemberBadge memberBadge = memberBadgeRepository.findByMember_MemberId(memberId).orElseThrow(
                () -> new IllegalArgumentException("멤버 뱃지 정보를 찾을 수 없습니다."));
        Badge nextBadge = badgeRepository.findById(
                memberBadge.getBadge().getBadgeId() != 5 ?
                        memberBadge.getBadge().getBadgeId() + 1 : 5
        ).orElseThrow(() -> new IllegalArgumentException("다음 뱃지 정보를 찾을 수 없습니다."));
        if (point.getWholePoint() >= nextBadge.getRequirement() && memberBadge.getBadge().getBadgeId() != 5) {
            memberBadge.updateBadge(nextBadge);
        }

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
        validate.setCreatedAt();
        validateRepository.save(validate);

        // 필요한 엔티티 조회
        Category category = categoryRepository.findByCategoryName(CategoryName.CAR).orElseThrow(
                () -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        Place place = placeRepository.findByCategory_CategoryId(category.getCategoryId()).orElseThrow(
                () -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        Member member = Member.builder()
                .memberId(memberId)
                .build();

        // Point, carbonSave 계산
        Long pointAmount = (long) ((double) carRequest.getChargeFee() / 100);
        Long carbonSave = (long) (Math.ceil((double) carRequest.getChargeAmount() / 7));

        // History 생성 및 저장
        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .chargeAmount(carRequest.getChargeAmount())
                .purchaseAmount(carRequest.getChargeFee())
                .carbonSave(carbonSave)
                .build();
        history.setCreatedAt();
        historyRepository.save(history);

        // PointHistory 생성 및 저장
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .category(category)
                .pointAmount(pointAmount)
                .description("전기차/수소차 총전소 이용 인증")
                .logId(history.getHistoryId())
                .build();
        pointHistory.setCreatedAt();
        pointHistoryRepository.save(pointHistory);

        // Member의 Point, carbonSave 업데이트
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));
        point.addPoint(pointAmount);
        point.addCarbonSaveTotal(carbonSave);

        // 뱃지 최신화
        MemberBadge memberBadge = memberBadgeRepository.findByMember_MemberId(memberId).orElseThrow(
                () -> new IllegalArgumentException("멤버 뱃지 정보를 찾을 수 없습니다."));
        Badge nextBadge = badgeRepository.findById(
                memberBadge.getBadge().getBadgeId() != 5 ?
                        memberBadge.getBadge().getBadgeId() + 1 : 5
        ).orElseThrow(() -> new IllegalArgumentException("다음 뱃지 정보를 찾을 수 없습니다."));
        if (point.getWholePoint() >= nextBadge.getRequirement() && memberBadge.getBadge().getBadgeId() != 5) {
            memberBadge.updateBadge(nextBadge);
        }

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
        validate.setCreatedAt();
        validateRepository.save(validate);

        // 필요한 엔티티 조회
        String placeName = shopRequest.getName();
        CategoryName categoryName = null;
        String description = null;
        if (shopRequest.getCategory().equals("recycle")) {
            categoryName = CategoryName.RECYCLING_CENTER;
            description = "재활용센터 이용 인증";
        } else if (shopRequest.getCategory().equals("zero")) {
            categoryName = CategoryName.ZERO_WASTE;
            description = "제로웨이스트 가게 이용 인증";
        } else {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
        Category category = categoryRepository.findByCategoryName(categoryName).orElseThrow(
                () -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        Place place = placeRepository.findByPlaceName(placeName).orElseThrow(
                () -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        Member member = Member.builder()
                .memberId(memberId)
                .build();

        // Point, carbonSave 계산
        Long pointAmount = (long) ((double) shopRequest.getPrice() / 100);
        Long carbonSave = (long) (Math.ceil((double) shopRequest.getPrice() / 20000));

        // History 생성 및 저장
        History history = History.builder()
                .member(member)
                .place(place)
                .category(category)
                .purchaseAmount(shopRequest.getPrice())
                .carbonSave(carbonSave)
                .build();
        history.setCreatedAt();
        historyRepository.save(history);

        // PointHistory 생성 및 저장
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .category(category)
                .pointAmount(pointAmount)
                .description(description)
                .logId(history.getHistoryId())
                .build();
        pointHistory.setCreatedAt();
        pointHistoryRepository.save(pointHistory);

        // Member의 Point, carbonSave 업데이트
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow();
        point.addPoint(pointAmount);
        point.addCarbonSaveTotal(carbonSave);

        // 뱃지 최신화
        MemberBadge memberBadge = memberBadgeRepository.findByMember_MemberId(memberId).orElseThrow(() ->
                new IllegalArgumentException("멤버 뱃지 정보를 찾을 수 없습니다."));
        Badge nextBadge = badgeRepository.findById(
                memberBadge.getBadge().getBadgeId() != 5 ?
                        memberBadge.getBadge().getBadgeId() + 1 : 5
        ).orElseThrow(() -> new IllegalArgumentException("다음 뱃지 정보를 찾을 수 없습니다."));
        if (point.getWholePoint() >= nextBadge.getRequirement() && memberBadge.getBadge().getBadgeId() != 5) {
            memberBadge.updateBadge(nextBadge);
        }

        // Response 반환
        return VerificationResponse.builder()
                .point(pointAmount)
                .carbonSave(carbonSave)
                .build();
    }

    @Override
    public VerificationHistoryResponse getVerificationHistory(Long memberId) {
        // 인증 내역 조회 (따릉이, 전기차/수소차, 재활용센터, 제로웨이스트)
        List<History> historyList = historyRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);

        // 인증 내역을 Response 형식으로 저장할 List 생성
        List<VerificationHistoryResponse.VerificationHistoryItem> verificationHistoryItems = new ArrayList<>();

        // 각 인증 내역에 대해 PointHistory 조회 및 Response 형식으로 변환 (수정 필요)
        for (History history : historyList) {
            PointHistory pointHistory = pointHistoryRepository.findByLogId(history.getHistoryId()).orElseThrow(() ->
                    new IllegalArgumentException("포인트 히스토리를 찾을 수 없습니다."));
            verificationHistoryItems.add(VerificationHistoryResponse.VerificationHistoryItem.builder()
                    .category(history.getCategory().getCategoryName().toString())
                    .createdAt(history.getCreatedAt().toString())
                    .point(pointHistory.getPointAmount())
                    .build());
        }

        // Response 반환
        return VerificationHistoryResponse.builder()
                .historyItems(verificationHistoryItems)
                .build();
    }

    @Override
    public MonthlyVerificationResponse getMonthlyVerification(Long memberId) {
        // 이번달 Point 정보 조회
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow(
                () -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));

        // Response 반환
        return MonthlyVerificationResponse.builder()
                .verifyTimes(point.getPointTimes())
                .pointSum(point.getMonthPoint())
                .build();
    }
}
