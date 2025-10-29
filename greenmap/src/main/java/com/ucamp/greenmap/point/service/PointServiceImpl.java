package com.ucamp.greenmap.point.service;

import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.domain.PointHistory;
import com.ucamp.greenmap.point.domain.Voucher;
import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import com.ucamp.greenmap.point.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final VoucherRepository voucherRepository;
    private final CategoryRepository categoryRepository;
    private final PointHistoryRepository pointHistoryRepository;

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
        point.changePoint(beforePoint - usedPoint);
        point.updateUsedPoint(point.getUsedPoint() + usedPoint);

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
        history.setCreatedAt(LocalDateTime.now());

        pointHistoryRepository.save(history);

        return usedPoint;
    }
}
