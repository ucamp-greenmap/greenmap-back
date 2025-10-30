package com.ucamp.greenmap.point.service;

import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.*;

public interface PointService {

    Long usePoint(UsePointRequest request, Long memberId);

    UserInfoResponse getPointInfo(Long memberId);

    ShopInfoDto getShopInfo(Long memberId);

    UsedPointLogResponse getUsedPointLogs(Long memberId);

    RankingResponse getRanking(Long memberId);

    UserPointInfo getUserPointInfo(Long memberId);

    CarbonInfoResponse getCarbonInfo(Long memberId);
}
