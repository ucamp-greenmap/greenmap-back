package com.ucamp.greenmap.badge.service;

import com.ucamp.greenmap.badge.dto.request.BadgeRequest;
import com.ucamp.greenmap.badge.dto.response.BadgeResponse;

public interface BadgeService {
    BadgeResponse searchBadges(Long memberId);

    String addBadge(BadgeRequest request);

    String selectBadges(Long memberId, String badgeName);
}
