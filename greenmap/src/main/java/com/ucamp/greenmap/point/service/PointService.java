package com.ucamp.greenmap.point.service;

import com.ucamp.greenmap.point.dto.request.UsePointRequest;

public interface PointService {

    Long usePoint(UsePointRequest request, Long memberId);
}
