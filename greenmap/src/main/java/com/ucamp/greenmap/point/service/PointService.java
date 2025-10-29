package com.ucamp.greenmap.point.service;

import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.ShopInfoDto;
import com.ucamp.greenmap.point.dto.response.UsedPointLogResponse;
import com.ucamp.greenmap.point.dto.response.UserInfoResponse;

public interface PointService {

    Long usePoint(UsePointRequest request, Long memberId);

    UserInfoResponse getPointInfo(Long memberId);

    ShopInfoDto getShopInfo(Long memberId);

    UsedPointLogResponse getUsedPointLogs(Long memberId);
}
