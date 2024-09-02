package com.attica.athens.domain.member.dao;


import com.attica.athens.domain.member.domain.TempMember;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempMemberRepository extends JpaRepository<TempMember, Long> {

    Optional<TempMember> findByUuid(UUID uuid);
}
