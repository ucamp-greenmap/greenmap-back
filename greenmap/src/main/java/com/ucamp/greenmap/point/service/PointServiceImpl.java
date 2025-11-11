package com.ucamp.greenmap.point.service;

import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.domain.PointHistory;
import com.ucamp.greenmap.point.domain.Voucher;
import com.ucamp.greenmap.point.dto.request.ShopRequest;
import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.*;
import com.ucamp.greenmap.point.enums.Type;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import com.ucamp.greenmap.point.repository.VoucherRepository;
import com.ucamp.greenmap.verification.dto.response.HistoryCarbonDto;
import com.ucamp.greenmap.verification.repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final VoucherRepository voucherRepository;
    private final CategoryRepository categoryRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final HistoryRepository historyRepository;
    private final ImageRepository imageRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    @Override
    @Transactional
    public Long usePoint(UsePointRequest request, Long memberId) {

        // 1) 포인트 조회
        Point point = pointRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 포인트 정보가 없습니다."));

        Long beforePoint = point.getPoint();

        // 2) 사용 금액 결정 + 부가정보
        final Long usedPoint;
        final String description;
        final Long logId;
        final CategoryName catName;

        switch (request.getType()) {
            case VOUCHER -> {
                Long voucherId = request.getPoint();
                Voucher voucher = voucherRepository.findById(voucherId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 바우처가 존재하지 않습니다."));
                usedPoint = voucher.getPrice();
                description = voucher.getName() + " 기프티콘";
                logId = voucherId;
                catName = CategoryName.VOUCHER;
            }
            case CASH -> {
                usedPoint = request.getPoint();
                description = "계좌 입금";
                logId = null; // 현금화는 참조 로그 없음
                catName = CategoryName.CASH;
            }
            default -> throw new IllegalArgumentException("해당 타입의 포인트 사용은 지원하지 않습니다.");
        }

        if (beforePoint < usedPoint) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        // 3) 잔액/사용 누적 반영
        point.usePoint(usedPoint);

        // 4) 카테고리 조회
        Category category = categoryRepository.findByCategoryName(catName)
                .orElseThrow(() -> new IllegalStateException("카테고리(" + catName + ")가 존재하지 않습니다."));

        // 5) 히스토리 적재 (사용 = 음수)
        PointHistory history = PointHistory.builder()
                .member(point.getMember())
                .category(category)
                .pointAmount(-usedPoint)
                .description(description)
                .logId(logId)              // CASH면 null
                .build();
        history.setCreatedAt();

        pointHistoryRepository.save(history);

        return usedPoint;
    }

    @Override
    public UserInfoResponse getPointInfo(Long memberId) {
        Point point = pointRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 포인트 정보가 없습니다."));

        MemberBadge mb = memberBadgeRepository.findSelectedBadge(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 선택된 배지 정보가 없습니다."));

        return UserInfoResponse.builder()
                .carbon_save(point.getCarbonSaveTotal())
                .point(point.getPoint())
                .badgeUrl(mb.getBadge().getImage().getImageUrl())
                .build();
    }

    @Override
    public ShopInfoDto getShopInfo(Long memberId) {
        Point point = pointRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 포인트 정보가 없습니다."));

        // 1) 바우처 엔티티 조회
        List<Voucher> vouchers = voucherRepository.findAll();

        // 2) 바우처 DTO 변환
        List<VoucherDto> voucherList = vouchers.stream()
                .map(VoucherDto::fromEntity)
                .toList();

        // 3) ShopInfoDto 생성 및 반환
        return ShopInfoDto.of(point.getPoint(), voucherList);
    }

    @Override
    public UsedPointLogResponse getUsedPointLogs(Long memberId) {

        List<PointHistory> histories = pointHistoryRepository.findTop5ByMember_MemberIdOrderByCreatedAtDesc(memberId);

        List<UsedPointLog> usedPointLogs = histories.stream()
                .map(history -> UsedPointLog.builder()
                        .pointAmount(history.getPointAmount())
                        .description(history.getDescription())
                        .date(history.getCreatedAt())
                        .category(null) // 사용 로그는 카테고리 정보가 필요 없으므로 null 처리
                        .build())
                .toList();
        return UsedPointLogResponse.builder()
                .usedLogs(usedPointLogs)
                .memberId(memberId)
                .build();
    }

    @Override
    public RankingResponse getRanking(Long memberId) {
        Point memberPoint = pointRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 포인트 정보가 없습니다."));

        List<Point> topRanks = pointRepository.findTop10ByMember_IsActiveTrueOrderByMonthPointDesc();

        long myRank = pointRepository.findMemberRank(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 순위를 찾을 수 없습니다."));

        List<Long> memberIds = topRanks.stream()
                .map(p -> p.getMember().getMemberId())
                .toList();

        Map<Long, String> badgeUrlMap = memberIds.isEmpty()
                ? Collections.emptyMap()
                : memberBadgeRepository.findSelectedBadges(memberIds).stream()
                .collect(Collectors.toMap(
                        mb -> mb.getMember().getMemberId(),
                        mb -> mb.getBadge().getImage() != null ? mb.getBadge().getImage().getImageUrl() : null,
                        (prev, curr) -> prev
                ));

        List<Ranking> ranks = topRanks.stream()
                .map(point -> Ranking.builder()
                        .memberId(point.getMember().getMemberId())
                        .nickname(point.getMember().getNickname())
                        .point(point.getMonthPoint())
                        .carbonSave(point.getCarbonSaveTotal())
                        .imageUrl(point.getMember().getImage().getImageUrl())
                        .badgeUrl(badgeUrlMap.getOrDefault(point.getMember().getMemberId(), null))
                        .build())
                .toList();


        return RankingResponse.builder()
                .memberId(memberId)
                .nickname(memberPoint.getMember().getNickname())
                .memberPoint(memberPoint.getMonthPoint())
                .carbonSave(memberPoint.getCarbonSaveTotal())
                .imageUrl(memberPoint.getMember().getImage().getImageUrl())
                .rank(myRank)
                .ranks(ranks)
                .build();
    }

    @Override
    public UserPointInfo getUserPointInfo(Long memberId, Type type) {

        Point point = pointRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 포인트 정보가 없습니다."));

        List<PointHistory> histories = switch (type) {
            case Used -> pointHistoryRepository
                    .findByMember_MemberIdAndCategory_CategoryIdInOrderByCreatedAtDesc(memberId, List.of(7L, 8L));
            case Get -> pointHistoryRepository
                    .findByMember_MemberIdAndCategory_CategoryIdInOrderByCreatedAtDesc(memberId, List.of(1L, 2L, 3L, 4L, 5L, 6L, 9L));
            case All -> pointHistoryRepository
                    .findByMember_MemberIdOrderByCreatedAtDesc(memberId);
        };

        List<UsedPointLog> usedPointLogs = histories.stream()
                .map(history -> {
                    String categoryName = switch (history.getCategory().getCategoryId().intValue()) {
                        case 9 -> "챌린지";
                        case 6 -> "뉴스";
                        case 7, 8 -> "교환";
                        default -> "인증";
                    };

                    return UsedPointLog.builder()
                            .pointAmount(history.getPointAmount())
                            .description(history.getDescription())
                            .date(history.getCreatedAt())
                            .category(categoryName)
                            .build();
                })
                .toList();


        return UserPointInfo.builder()
                .memberId(memberId)
                .getPoint(point.getWholePoint())
                .usedPoint(point.getUsedPoint())
                .logs(usedPointLogs)
                .build();
    }

    @Override
    public CarbonInfoResponse getCarbonInfo(Long memberId) {
        List<PointHistory> histories =
                pointHistoryRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);

        // 1) logId 수집 (null 제거)
        List<Long> logIds = histories.stream()
                .map(PointHistory::getLogId)
                .filter(Objects::nonNull) // logId가 null인 경우 -> 포인트 사용 내역이므로 제외
                .distinct()
                .toList();

        // 2) 한 번의 쿼리로 carbonSave 로딩 → Map<Long historyId, Long carbonSave>
        List<HistoryCarbonDto> carbonList = historyRepository.findCarbonByIds(logIds);
        Map<Long, Long> carbonMap = carbonList.stream()
                .collect(Collectors.toMap(
                        HistoryCarbonDto::getHistoryId,
                        dto -> dto.getCarbonSave() != null ? dto.getCarbonSave() : 0L
                ));

        long car = 0L, recycle = 0L, bike = 0L, zero = 0L;

        // 3) 합산
        for (PointHistory ph : histories) {
            Long logId = ph.getLogId();
            if (logId == null) continue;

            Long saved = carbonMap.getOrDefault(logId, 0L);
            Long catId = ph.getCategory() != null ? ph.getCategory().getCategoryId() : null;

            switch (catId != null ? catId.intValue() : -1) {
                case 1 -> bike += saved; // 따릉이
                case 2 -> zero += saved; // 제로웨이스트
                case 3, 4 -> car += saved; // 전기차/수소차
                case 5 -> recycle += saved; // 재활용센터
                default -> { /* 무시 */ }
            }
        }
        return CarbonInfoResponse.builder()
                .carbonSave(car + recycle + bike + zero)
                .car(car)
                .recycle(recycle)
                .bike(bike)
                .zero(zero)
                .build();
    }

    @Override
    public ShopAddResponse addShopVoucher(ShopRequest request, Long memberId) {
        Image image = imageRepository.findByImageUrl(request.getImageUrl())
                .orElseGet(() -> {
                    Image newImage = Image.builder()
                            .imageUrl(request.getImageUrl())
                            .build();
                    return imageRepository.save(newImage);
                });

        Voucher voucher = Voucher.builder()
                .image(image)
                .price(request.getPrice())
                .name(request.getName())
                .category(request.getCategory())
                .brand(request.getBrand())
                .popular(request.getPopular())
                .build();

        return ShopAddResponse.builder()
                .voucherId(voucherRepository.save(voucher).getVoucherId())
                .imageUrl(voucher.getImage().getImageUrl())
                .price(voucher.getPrice())
                .name(voucher.getName())
                .category(voucher.getCategory())
                .brand(voucher.getBrand())
                .popular(voucher.getPopular())
                .build();
    }

//    @Override
//    public MyRankingResponse getMyRanking(Long memberId) {
//        Point memberPoint = pointRepository.findByMember_MemberId(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 포인트 정보가 없습니다."));
//
//        long myRank = pointRepository.countByMonthPointGreaterThan(memberPoint.getMonthPoint()) + 1;
//
//        return MyRankingResponse.builder()
//                .memberId(memberId)
//                .nickname(memberPoint.getMember().getNickname())
//                .memberPoint(memberPoint.getMonthPoint())
//                .carbonSave(memberPoint.getCarbonSaveTotal())
//                .imageUrl(memberPoint.getMember().getImage().getImageUrl())
//                .rank(myRank)
//                .build();
//    }

}
