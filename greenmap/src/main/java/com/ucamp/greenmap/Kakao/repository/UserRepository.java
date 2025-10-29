package com.ucamp.greenmap.Kakao.repository;

import com.ucamp.greenmap.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Member, Long> {

    // 이메일로 유저 조회
    Optional<Member> findByEmail(String email);
}
