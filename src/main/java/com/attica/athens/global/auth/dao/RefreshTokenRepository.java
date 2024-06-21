package com.attica.athens.global.auth.dao;

import com.attica.athens.global.auth.domain.RefreshToken;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<Boolean> existsByRefresh(String refresh);

    List<RefreshToken> findByExpirationBefore(Date date);

}
