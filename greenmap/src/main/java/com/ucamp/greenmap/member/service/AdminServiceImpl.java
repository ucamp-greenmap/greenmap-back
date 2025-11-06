package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.response.AdminResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final MemberRepository memberRepository;

    public AdminResponse getAdmin(Long memberId) {
        boolean isExist = memberRepository.isMemberOne(memberId);
        return AdminResponse.builder()
                .result(isExist)
                .build();
    }

}
