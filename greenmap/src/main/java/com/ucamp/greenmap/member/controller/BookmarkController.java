package com.ucamp.greenmap.member.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.BookmarkRequest;
import com.ucamp.greenmap.member.dto.response.BookmarkListResponse;
import com.ucamp.greenmap.member.dto.response.BookmarkResponse;
import com.ucamp.greenmap.member.service.BookmarkService;
import com.ucamp.greenmap.member.service.BookmarkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkServiceImpl bookmarkService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookmarkResponse>> toggleBookmark(
            @RequestBody BookmarkRequest request,
            @AuthenticationPrincipal Long memberId)
    {
        BookmarkResponse response = bookmarkService.toggleBookmark(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("북마크 처리 완료", response));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<BookmarkListResponse>>> getMyBookmarks(
            @AuthenticationPrincipal Long memberId
    ) {
        List<BookmarkListResponse> response = bookmarkService.getMyBookmarks(memberId);
        return ResponseEntity.ok(ApiResponse.success("북마크 목록 조회 성공", response));
    }

}