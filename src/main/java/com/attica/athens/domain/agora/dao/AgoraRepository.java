package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Agora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgoraRepository extends JpaRepository<Agora, Long>, AgoraQueryRepository {
}
