package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.response.MyPageResponse;

public interface MyPageService {

    MyPageResponse getMyPage(Long memberId);

}
