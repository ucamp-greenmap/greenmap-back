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
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.repository.MemberRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final PointRepository pointRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

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
                    // 획득한 뱃지인 경우 정보 업데이트 -> isActive가 false면 획득한 뱃지
                    if (!mb.getIsActive()) {
                        isAcquired = true;
                    }
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
        // 카테고리 조회
        log.info("request.getDesc 어쩌구 : " + request.getDescription().split(" ")[0]);
        Category category = categoryRepository.findByCategoryName(switch (request.getDescription().split(" ")[0]) {
            case "따릉이" -> CategoryName.BIKE;
            case "전기차" -> CategoryName.EVCAR;
            case "수소차" -> CategoryName.HCAR;
            case "재활용센터" -> CategoryName.RECYCLING_CENTER;
            case "제로웨이스트" -> CategoryName.ZERO_WASTE;
            default -> throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }).orElseThrow(() -> new IllegalStateException("해당 카테고리가 DB에 없습니다."));

        // 이미지 저장
        Image image = Image.builder()
                .imageUrl(request.getImage_url())
                .build();
        imageRepository.save(image);

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

        // 모든 멤버에 대해서 새 뱃지에 대한 멤버뱃지 추가
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            memberBadgeRepository.save(MemberBadge.builder()
                    .badge(badge)
                    .member(member)
                    .progress(0L)
                    .isSelected(false)
                    .build()
            );
        }

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
