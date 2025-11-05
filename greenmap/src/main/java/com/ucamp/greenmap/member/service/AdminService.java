package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.response.AdminResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface AdminService {
    AdminResponse getAdmin(Long memberId);

}
