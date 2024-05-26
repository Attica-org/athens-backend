package com.attica.athens.global.security.refresh.dao;

import com.attica.athens.global.security.refresh.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefresh(String refresh);

    Optional<RefreshToken> findById(Long id);

    @Transactional
    void deleteByRefresh(String refresh);

}
