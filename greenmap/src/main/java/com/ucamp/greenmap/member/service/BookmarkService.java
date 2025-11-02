package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.dto.request.BookmarkRequest;
import com.ucamp.greenmap.member.dto.response.BookmarkResponse;

public interface BookmarkService {
    BookmarkResponse toggleBookmark(Long memberId, BookmarkRequest request);
}