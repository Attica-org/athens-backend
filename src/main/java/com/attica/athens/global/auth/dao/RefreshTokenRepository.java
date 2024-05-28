package com.attica.athens.global.auth.dao;

import com.attica.athens.global.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<Boolean> existsByRefresh(String refresh);

    Optional<RefreshToken> findById(Long id);

    @Transactional
    void deleteByRefresh(String refresh);

}
