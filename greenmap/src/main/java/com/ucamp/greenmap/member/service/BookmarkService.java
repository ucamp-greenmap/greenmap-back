package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.request.BookmarkRequest;
import com.ucamp.greenmap.member.dto.response.BookmarkListResponse;
import com.ucamp.greenmap.member.dto.response.BookmarkResponse;

import java.util.List;

public interface BookmarkService {
    BookmarkResponse toggleBookmark(Long memberId, BookmarkRequest request);

    List<BookmarkListResponse> getMyBookmarks(Long memberId);
}