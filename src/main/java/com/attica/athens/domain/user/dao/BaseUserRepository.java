package com.attica.athens.domain.user.dao;

import com.attica.athens.domain.user.domain.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseUserRepository extends JpaRepository<BaseUser, Long> {
}
