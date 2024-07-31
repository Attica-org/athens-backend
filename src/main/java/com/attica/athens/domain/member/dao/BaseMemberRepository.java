package com.attica.athens.domain.member.dao;

import com.attica.athens.domain.member.domain.BaseMember;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseMemberRepository extends JpaRepository<BaseMember, Long> {

    Optional<BaseMember> findByUuid(UUID uuid);
}
