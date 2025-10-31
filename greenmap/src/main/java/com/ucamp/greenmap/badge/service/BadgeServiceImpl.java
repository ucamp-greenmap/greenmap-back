package com.ucamp.greenmap.badge.service;

import com.ucamp.greenmap.badge.repository.BadgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    private final BadgeRepository badgeRepository;


}
