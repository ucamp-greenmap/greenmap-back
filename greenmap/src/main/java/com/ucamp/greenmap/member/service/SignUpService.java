package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.request.SignUpRequest;
import com.ucamp.greenmap.member.dto.response.SignUpResponse;

public interface SignUpService {
    SignUpResponse signup(SignUpRequest request);
}
