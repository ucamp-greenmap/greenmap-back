package com.ucamp.greenmap.member.repository;

import com.ucamp.greenmap.member.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByMember_MemberIdAndPlace_PlaceId(long memberId, Long placeId);
}
