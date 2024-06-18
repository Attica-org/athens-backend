package com.attica.athens.global.auth.dao;

import com.attica.athens.global.auth.domain.RefreshToken;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<Boolean> existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    List<RefreshToken> findByExpirationBefore(Date date);

}
