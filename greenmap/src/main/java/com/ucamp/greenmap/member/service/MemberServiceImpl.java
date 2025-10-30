package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;


    @Override
    public MemberResponse deleteUser(MemberRequest request, Long memberId) {
        Optional<Member> member =  memberRepository.findByMemberId(memberId);
        Member newMember= Member.builder()
                .memberId(memberId)
                .email(member.get().getEmail())
                .nickname(member.get().getNickname())
                .image(member.get().getImage())
                .build();
        member.get().setUpdatedAt(LocalDateTime.now());
        member.get().setIsActive(!member.get().getIsActive());
        memberRepository.save(newMember);
    return MemberResponse.builder()
            .email(newMember.getEmail())
            .isActive(newMember.getIsActive())
            .memberId(newMember.getMemberId())
            .image(newMember.getImage())
            .build();
    }

    @Override
    public MemberResponse updateMember(MemberRequest request, Long memberId){
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        Member newMember = Member.builder()
                .memberId(memberId)
                .email(member.get().getEmail())
                .nickname(member.get().getNickname())
                .image(member.get().getImage())
                .build();
        memberRepository.save(newMember);
        return MemberResponse.builder()
            .email(newMember.getEmail())
                .isActive(newMember.getIsActive())
                .memberId(newMember.getMemberId())
                .image(newMember.getImage())
                .build();
    }
}
