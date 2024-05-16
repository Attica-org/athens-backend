package com.attica.athens.domain.agoraUser.dao;


import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgoraUserRepository extends JpaRepository<AgoraUser, Integer> {

    Optional<AgoraUser> findByAgora_IdAndUser_Id(Long agoraId, Long userId);
}
