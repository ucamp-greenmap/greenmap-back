package com.ucamp.greenmap.member.repository;

import com.ucamp.greenmap.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // 이메일 중복확인
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Member m WHERE m.email = :email AND m.isActive = true")
    boolean existsByEmail(@Param("email") String email);

    // 닉네임 중복확인
    boolean existsByNickname(String nickname);

    // 이메일로 유저 조회
    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isActive = true")
    Optional<Member> findByEmail( @Param("email") String email);

    // 카카오 로그인용 (카카오 ID로 조회)
    Optional<Member> findByKakaoId(Long kakaoId);

   boolean existsById(Long memberId);

}
