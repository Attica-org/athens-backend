package com.attica.athens.domain.member.dao;


import com.attica.athens.domain.member.domain.BaseMember;
import com.attica.athens.domain.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    Optional<Member> findById(Long userId);

    Optional<Member> findByOauthId(String oauthId);

    @Query("SELECT am.member FROM AgoraMember am WHERE am.id = :agoraMemberId")
    Optional<BaseMember> findMemberByAgoraMemberId(@Param("agoraMemberId") Long agoraMemberId);

    @Query("SELECT m FROM Member m WHERE m.isDeleted = true AND m.deletedAt < :date")
    List<Member> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime date);
}
