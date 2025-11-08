package com.ucamp.greenmap.point.service;

import com.ucamp.greenmap.point.dto.request.ShopRequest;
import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.*;
import com.ucamp.greenmap.point.enums.Type;

public interface PointService {

    Long usePoint(UsePointRequest request, Long memberId);

    UserInfoResponse getPointInfo(Long memberId);

    ShopInfoDto getShopInfo(Long memberId);

    UsedPointLogResponse getUsedPointLogs(Long memberId);

    RankingResponse getRanking(Long memberId);

    UserPointInfo getUserPointInfo(Long memberId, Type type);

    CarbonInfoResponse getCarbonInfo(Long memberId);

    ShopAddResponse addShopVoucher(ShopRequest request, Long memberId);

//    MyRankingResponse getMyRanking(Long memberId);
}
