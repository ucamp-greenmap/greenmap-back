package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.domain.Bookmark;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.request.BookmarkRequest;
import com.ucamp.greenmap.member.dto.response.BookmarkListResponse;
import com.ucamp.greenmap.member.dto.response.BookmarkResponse;
import com.ucamp.greenmap.member.repository.BookmarkRepository;
import com.ucamp.greenmap.member.repository.MemberRepository;
import com.ucamp.greenmap.place.domain.Place;
import com.ucamp.greenmap.place.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    @Override
    @Transactional
    public BookmarkResponse toggleBookmark(Long memberId, BookmarkRequest request) {
            // 1. 회원 확인
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

            // 2. 장소 확인
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new RuntimeException("PLACE NOT FOUND"));

            // 3. 현재 북마크 존재 여부 확인
            Optional<Bookmark> existingBookmark = bookmarkRepository
                    .findByMember_MemberIdAndPlace_PlaceId(memberId, request.getPlaceId());

            // 4. 없으면 INSERT
        if (existingBookmark.isEmpty()) {
            bookmarkRepository.save(
                    Bookmark.builder()
                            .member(member)
                            .place(place)
                            .build()
            );

            return BookmarkResponse.builder()
                    .bookmarked(true)
                    .build();
        }

        bookmarkRepository.delete(existingBookmark.get());

        return BookmarkResponse.builder()
                .bookmarked(false)
                .build();
        }

    @Override
    public List<BookmarkListResponse> getMyBookmarks(Long memberId) {

        List<Bookmark> bookmarks = bookmarkRepository.findByMember_MemberId(memberId);

        return bookmarks.stream()
                .map(b -> {
                    var place = b.getPlace();
                    var opening = place.getOpeningHours();

                    return BookmarkListResponse.builder()
                            .placeId(place.getPlaceId())
                            .placeName(place.getPlaceName())
                            .address(place.getAddress())
                            .detailAddress(place.getDetailAddress())
                            .locationX(place.getLocationX())
                            .locationY(place.getLocationY())
                            .telNum(place.getTelNum())
                            .weekdayOpen(opening != null ? opening.getWeekdayOpen() : null)
                            .weekdayClose(opening != null ? opening.getWeekdayClose() : null)
                            .weekendOpen(opening != null ? opening.getWeekendOpen() : null)
                            .weekendClose(opening != null ? opening.getWeekendClose() : null)
                            .openingDays(opening != null ? opening.getOpeningDays() : null)
                            .build();
                })
                .toList();
    }

}