package com.ucamp.greenmap.member.repository;

import com.ucamp.greenmap.member.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByMember_MemberIdAndPlace_PlaceId(Long memberId, Long placeId);
    Optional<Bookmark> findByMember_MemberIdAndPlace_PlaceId(Long memberId, Long placeId);
    void deleteByMember_MemberIdAndPlace_PlaceId(Long memberId, Long placeId);
}
