package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.dto.response.MyPageResponse;
import com.ucamp.greenmap.member.dto.response.RecodeResponse;
import com.ucamp.greenmap.point.dto.request.MostActiveCategory;
import com.ucamp.greenmap.point.dto.response.RankingResponse;
import com.ucamp.greenmap.point.dto.response.UserInfoResponse;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.service.PointService;
import com.ucamp.greenmap.verification.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final MemberService memberService;
    private final HistoryRepository historyRepository;
    private final PointService pointService;

    @Override
    public MyPageResponse getMyPage(Long memberId) {
        MemberResponse member = memberService.getMyInfo(memberId);
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

    @Override
    public RecodeResponse getRecode(Long memberId) {
        MemberResponse member = memberService.getMyInfo(memberId);

        Integer curVerify = historyRepository.countThisMonth(memberId);
        Integer prevVerify = historyRepository.countLastMonth(memberId);

        var top = historyRepository.mostActiveCategoryThisMonth(memberId);

        return RecodeResponse.builder()
                .memberId(member.getMemberId())
                .verifyTimes(curVerify != null ? curVerify : 0)
                .timesDiff((curVerify != null ? curVerify : 0) -
                        (prevVerify != null ? prevVerify : 0))
                .mostKind(top != null && !top.isEmpty() ? top.get(0).getCategoryName() : null)
                .mostTimes(top != null && !top.isEmpty() ? top.get(0).getCount() : 0)
                .pointSum(0) // 너 포인트 sum 로직 넣을거면 여기
                .pointDiff(0) // 포인트 diff도 로직 있으면 넣기
                .build();
    }
}
