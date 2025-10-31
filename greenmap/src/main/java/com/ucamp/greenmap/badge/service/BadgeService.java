package com.ucamp.greenmap.badge.service;

import com.ucamp.greenmap.badge.dto.response.BadgeResponse;

public interface BadgeService {
    BadgeResponse searchBadges(Long memberId);
}
