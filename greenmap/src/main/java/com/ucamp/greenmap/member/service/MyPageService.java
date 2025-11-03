package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.response.MyPageResponse;
import com.ucamp.greenmap.member.dto.response.RecodeResponse;

public interface MyPageService {

    MyPageResponse getMyPage(Long memberId);

    RecodeResponse getRecode(Long memberId);

}
