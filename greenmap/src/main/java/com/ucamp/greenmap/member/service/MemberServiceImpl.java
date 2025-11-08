package com.ucamp.greenmap.member.service;

import com.nimbusds.jwt.JWT;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.dto.response.ImageResponse;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private  final ImageRepository imageRepository;

    //내 정보 조회
    public MemberResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        return MemberResponse.memberResponse(member);

    }

   //회원 상태 수정
    public MemberResponse deactivateUser(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));
        log.info("memberId : "+memberId);
        // 활성/비활성 토글
        member.setIsActive(Boolean.FALSE);
        member.setUpdatedAt(LocalDateTime.now());

        memberRepository.save(member);

        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .isActive(member.getIsActive())
                .image(ImageResponse.builder()
                        .imageId(member.getImage().getImageId())
                        .imageUrl(member.getImage().getImageUrl())
                        .build()
                )

                .build();
    }

    public MemberResponse updateUser(MemberRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 정보 수정
        if (request.getNickname() != null) {
            member.setNickname(request.getNickname());
        }
        member.setNickname(request.getNickname());
        member.setUpdatedAt(LocalDateTime.now());

        Member updated = memberRepository.save(member);
        return MemberResponse.memberResponse(updated);
    }

}
