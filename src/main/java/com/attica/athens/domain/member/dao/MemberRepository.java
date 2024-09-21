package com.attica.athens.domain.member.dao;


import com.attica.athens.domain.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    Optional<Member> findById(Long userId);

    Optional<Member> findByOauthId(String oauthId);
}
