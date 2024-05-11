package com.attica.athens.domain.user.dao;


import com.attica.athens.domain.user.domain.TempUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempUserRepository extends JpaRepository<TempUser, Integer> {

    Optional<TempUser> findByUuid(UUID uuid);
}
