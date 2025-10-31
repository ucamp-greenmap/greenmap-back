package com.ucamp.greenmap.badge.service;

import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.dto.response.BadgeResponse;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    @Override
    public BadgeResponse searchBadges(Long memberId) {
        // 모든 뱃지와 회원이 획득한 뱃지를 조회
        List<Badge> badgeList = badgeRepository.findAll();
        List<MemberBadge> memberBadgeList = memberBadgeRepository.findAllByMember_MemberId(memberId);
        List<BadgeResponse.BadgeItems> badgeItemsList = new ArrayList<>();

//        for (Badge badge : badgeList) {
//            BadgeResponse.BadgeItems.builder()
//                    .name(badge.getBadgeName())
//                    .description(badge.getDescription())
//                    .image_url(badge.getImage().getImageUrl())
//                    .created_at()
//                    .isFinish()
//                    .progress()
//                    .build();
//        }

        Long count = (long) memberBadgeList.size();
        Long total = (long) badgeList.size();
        Long successRate = total == 0 ? 0 : (count * 100) / total;

//        return BadgeResponse.builder()
//                .badge_count(count)
//                .total_badge(total)
//                .success_rate(successRate)
//                .badge_list()
//                .build();

        return null;
    }
}
