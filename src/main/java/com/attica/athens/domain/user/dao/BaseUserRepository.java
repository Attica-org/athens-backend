package com.attica.athens.domain.user.dao;

import com.attica.athens.domain.user.domain.BaseUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseUserRepository extends JpaRepository<BaseUser, Long> {

    Optional<BaseUser> findByUuid(UUID uuid);
}
