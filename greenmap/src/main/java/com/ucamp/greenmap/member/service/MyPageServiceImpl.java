package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.dto.response.MyPageResponse;
import com.ucamp.greenmap.member.dto.response.RecodeResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.dto.response.MyRankingResponse;
import com.ucamp.greenmap.point.dto.response.RankingResponse;
import com.ucamp.greenmap.point.dto.response.UserInfoResponse;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import com.ucamp.greenmap.point.service.PointService;
import com.ucamp.greenmap.verification.dto.response.CategoryCount;
import com.ucamp.greenmap.verification.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final MemberService memberService;
    private final HistoryRepository historyRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    @Override
    public MyPageResponse getMyPage(Long memberId) {
        MemberResponse member = memberService.getMyInfo(memberId);
        Point point = pointRepository.findByMember_MemberId(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 멤버의 포인트 정보가 없습니다."));
        long rank = pointRepository.findMemberRank(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 순위를 찾을 수 없습니다."));
        MemberBadge memberBadge = memberBadgeRepository.findSelectedBadge(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 멤버가 선택한 뱃지가 없습니다."));

        return MyPageResponse.builder()
                .member(
                        MyPageResponse.MemberInfo.builder()
                                .memberId(member.getMemberId())
                                .email(member.getEmail())
                                .nickname(member.getNickname())
                                .imageUrl(member.getImage().getImageUrl())
                                .badgeUrl(memberBadge.getBadge().getImage().getImageUrl())
                                .build()
                )
                .point(
                        MyPageResponse.PointInfo.builder()
                                .point(point.getPoint())
                                .carbonSave(point.getCarbonSaveTotal())
                                .build()
                )
                .ranking(
                        MyPageResponse.RankingInfo.builder()
                                .rank(rank)
                                .point(point.getMonthPoint())
                                .carbonSave(point.getCarbonSaveTotal())
                                .build()
                )
                .build();
    }


    @Override
    public RecodeResponse getRecode(Long memberId) {
        // 회원 정보
        MemberResponse member = memberService.getMyInfo(memberId);

        // 현재 포인트(누적)
        Point point = pointRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        Long curVerify = historyRepository.countThisMonth(memberId);
        Long prevVerify = historyRepository.countLastMonth(memberId);

        // 가장 많이 한 카테고리 + 횟수
        CategoryCount top = historyRepository.mostActiveCategoryThisMonth(memberId);
        String category = top != null ? top.getCategoryName() : null;
        Long count = top != null ? top.getCnt() : 0L;

        // 포인트 합산 (PointHistory 기반)
        Long prevPoint = pointHistoryRepository.sumLastMonthPoints(memberId);
        Long curPoint = pointHistoryRepository.sumThisMonthPoints(memberId);

        Long pointDiff = (curPoint != null ? curPoint : 0) -
                (prevPoint != null ? prevPoint : 0);

        return RecodeResponse.builder()
                .memberId(member.getMemberId())
                .verifyTimes(curVerify != null ? curVerify : 0)
                .timesDiff((curVerify != null ? curVerify : 0) -
                        (prevVerify != null ? prevVerify : 0))
                .mostKind(category)
                .mostTimes(count)
                .pointSum(point.getWholePoint())       // 누적포인트
                .pointDiff(pointDiff)                 // 포인트 차이
                .build();
    }


}
