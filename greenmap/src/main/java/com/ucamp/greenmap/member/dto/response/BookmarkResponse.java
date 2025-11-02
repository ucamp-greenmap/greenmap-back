package com.ucamp.greenmap.member.dto.response;

import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.place.domain.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BookmarkResponse {
    private Long bookmarkId;
    private Long memberId;
    private Long placeId;
    private boolean bookmarked;

}
