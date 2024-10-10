package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Agora;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgoraRepository extends JpaRepository<Agora, Long>, AgoraQueryRepository {
    Optional<Agora> findById(Long agoraId);
}
