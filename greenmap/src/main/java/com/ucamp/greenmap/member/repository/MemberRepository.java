package com.ucamp.greenmap.member.repository;

import com.ucamp.greenmap.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // 이메일 중복확인
    boolean existsByEmail(String email);

    // 닉네임 중복확인
    boolean existsByNickname(String nickname);

    // 이메일로 유저 조회
    Optional<Member> findByEmail(String email);

    // 카카오 로그인용 (카카오 ID로 조회)
    Optional<Member> findByKakaoId(Long kakaoId);

}
