package com.ucamp.greenmap.member.dto.request;

import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.place.domain.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookmarkRequest {
    private Long bookmarkId;
    private Member member;
    private Place place;
    private Long memberId;
    private Long placeId;
}
