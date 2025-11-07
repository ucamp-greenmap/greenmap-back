package com.ucamp.greenmap.badge.service;

import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.dto.request.BadgeRequest;
import com.ucamp.greenmap.badge.dto.response.BadgeResponse;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final PointRepository pointRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    @Override
    public BadgeResponse searchBadges(Long memberId) {
        // 전체 뱃지 정보 조회
        List<Badge> badges = badgeRepository.findAll();
        // 현재 멤버의 뱃지 정보 조회
        List<MemberBadge> memberBadge = memberBadgeRepository.findByMember_MemberId(memberId);

        List<BadgeResponse.BadgeInfo> badgeInfos = badges.stream().map(badge -> {
            // 기본 뱃지 정보 추출
            String name = badge.getBadgeName();
            Long progress = null;
            Long standard = badge.getRequirement();
            String description = badge.getDescription();
            String imageUrl = badge.getImage().getImageUrl();
            LocalDateTime createdAt = null;
            boolean isAcquired = false;
            boolean isSelected = false;

            // 멤버가 획득한 뱃지인지 확인
            for (MemberBadge mb : memberBadge) {
                if (mb.getBadge().getBadgeId().equals(badge.getBadgeId())) {
                    isAcquired = true;
                    progress = mb.getProgress();
                    createdAt = mb.getCreatedAt();
                    if (mb.getIsSelected()) {
                        isSelected = true;
                    }
                    break;
                }
            }

            return BadgeResponse.BadgeInfo.builder()
                    .name(name)
                    .progress(progress)
                    .standard(standard)
                    .description(description)
                    .image_url(imageUrl)
                    .created_at(badge.getCreatedAt())
                    .isAcquired(isAcquired)
                    .isSelected(isSelected)
                    .build();
        }).toList();

        // 응답 생성
        return BadgeResponse.builder()
                .message("뱃지 정보 조회 성공")
                .data(badgeInfos)
                .build();
    }

    @Override
    public String addBadge(BadgeRequest request) {
        // 카테고리 및 이미지 조회
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() ->
                new IllegalStateException("해당 카테고리가 DB에 없습니다."));
        Image image = imageRepository.findByImageUrl(request.getImage_url()).orElseThrow(() ->
                new IllegalStateException("이미지 정보가 존재하지 않습니다."));

        // 뱃지 엔티티 생성 및 저장
        Badge badge = Badge.builder()
                .badgeName(request.getName())
                .category(category)
                .image(image)
                .description(request.getDescription())
                .requirement(request.getRequirement())
                .build();
        badge.setCreatedAt();
        badgeRepository.save(badge);

        // 성공 응답 반환
        return "뱃지 추가 성공";
    }

    @Override
    public String selectBadges(Long memberId, String badgeName) {
        // 멤버의 뱃지 목록 조회
        List<MemberBadge> badgeList = memberBadgeRepository.findByMember_MemberId(memberId);

        // 뱃지 선택 및 나머지 뱃지 선택 해제
        for (MemberBadge mb : badgeList) {
            if (mb.getBadge().getBadgeName().equals(badgeName)) {
                mb.selectBadge();
            } else {
                mb.deselectBadge();
            }
        }

        // 성공 응답 반환
        return "뱃지 선택 성공";
    }
}
