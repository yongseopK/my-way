package com.healthmap.myway.member.repository;

import com.healthmap.myway.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    // 이메일로 회원정보 조회하는 메서드
    Optional<Member> findByEmail(String email);

    // 이메일 중복체크 메서드
    boolean existsByEmail(String email);
}
