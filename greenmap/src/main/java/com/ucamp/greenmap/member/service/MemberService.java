package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;

public interface MemberService {


    MemberResponse getMyInfo(String email);

    MemberResponse deactivateUser(String email);

    MemberResponse updateUser(MemberRequest request, String email);
}
