package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;

public interface MemberService {

    MemberResponse deleteUser(MemberRequest request, Long memberId);

    MemberResponse updateMember(MemberRequest request, Long memberId);
}
