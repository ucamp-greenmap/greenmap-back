package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;

public interface MemberService {


    MemberResponse getMyInfo(Long memberId);

    MemberResponse deactivateUser(Long memberId);

    MemberResponse updateUser(MemberRequest request, Long memberId);
}
