package com.attica.athens.domain.agora.vote.dao;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgoraVoteUserRepository extends JpaRepository<AgoraUser, Long> {

    Optional<AgoraUser> findByUserId(Long id);
}
