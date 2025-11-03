package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.image.dto.response.ImageResponse;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.dto.response.MyPageResponse;
import com.ucamp.greenmap.point.dto.response.RankingResponse;
import com.ucamp.greenmap.point.dto.response.UserInfoResponse;
import com.ucamp.greenmap.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final MemberService memberService;
    private final PointService pointService;

    public MyPageResponse getMyPage(Long memberId) {

        MemberResponse member= memberService.getMyInfo(memberId);
        UserInfoResponse point = pointService.getPointInfo(memberId);
        RankingResponse ranking = pointService.getRanking(memberId);


        return MyPageResponse.builder()
                .member(
                        MyPageResponse.MemberInfo.builder()
                                .memberId(member.getMemberId())
                                .email(member.getEmail())
                                .nickname(member.getNickname())
                                .imageUrl(member.getImage().getImageUrl())
                                .build()
                )
                .point(
                        MyPageResponse.PointInfo.builder()
                                .point(point.getPoint())
                                .carbonSave(point.getCarbon_save())
                                .build()
                )
                .ranking(
                        MyPageResponse.RankingInfo.builder()
                                .rank(ranking.getRank())
                                .point(ranking.getMemberPoint())
                                .carbonSave(ranking.getCarbonSave())
                                .build()
                )
                .build();


    }
}
