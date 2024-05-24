package com.attica.athens.domain.agoraUser.dao;


import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgoraUserRepository extends JpaRepository<AgoraUser, Integer> {

    Optional<AgoraUser> findByAgoraIdAndUserId(Long agoraId, Long userId);

    @Query("SELECT new com.attica.athens.domain.chat.dto.response.SendMetaResponse$ParticipantsInfo(au.type, COUNT(au)) "
            +
            "FROM AgoraUser au " +
            "WHERE au.agora.id = :agoraId " +
            "GROUP BY au.type")
    List<SendMetaResponse.ParticipantsInfo> countAgoraUsersByType(@Param("agoraId") Long agoraId);

    List<AgoraUser> findByAgoraId(Long agoraId);
}
