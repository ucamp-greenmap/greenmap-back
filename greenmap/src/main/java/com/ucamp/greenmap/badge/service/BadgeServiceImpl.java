package com.ucamp.greenmap.badge.service;

import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.dto.response.BadgeResponse;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final PointRepository pointRepository;

    @Override
    public BadgeResponse searchBadges(Long memberId) {
        // 현재 멤버의 뱃지 정보 조회
        MemberBadge memberBadge = memberBadgeRepository.findByMember_MemberId(memberId).orElseThrow();
        // 필요한 정보들 조회
        Long nowBadgeId = memberBadge.getBadge().getBadgeId();
        Badge badge = badgeRepository.findById(nowBadgeId).orElseThrow();
        Badge nextBadge = badgeRepository.findById(nowBadgeId != 5 ? nowBadgeId + 1 : 5).orElseThrow();
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow();

        // 응답 생성
        return BadgeResponse.builder()
                .name(badge.getBadgeName())
                .wholePoint(point.getWholePoint())
                .nextPoint((long) nextBadge.getRequirement())
                .description(badge.getDescription())
                .image_url(badge.getImage().getImageUrl())
                .created_at(badge.getCreatedAt())
                .build();
    }
}
