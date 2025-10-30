package com.ucamp.greenmap.member.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MemberController {

    private final MemberService memberService;

    @PutMapping
    public ResponseEntity<ApiResponse<MemberResponse>> deleteUser(@RequestBody MemberRequest request){
        Long memberId= request.getMemberId();
        MemberResponse response = memberService.deleteUser(request, memberId);
        return ResponseEntity.ok(ApiResponse.success("성공",response));
    }

//    @PutMapping
//    public ResponseEntity<ApiResponse<MemberResponse>> updateUser(@RequestBody MemberRequest request){
//        Long memberId= request.getMemberId();
//        MemberResponse response = memberService.deleteUser(request, memberId);
//        return ResponseEntity.ok(ApiResponse.success("성공",response));
//    }




}
