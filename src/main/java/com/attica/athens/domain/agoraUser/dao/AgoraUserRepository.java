package com.attica.athens.domain.agoraUser.dao;


import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.agoraUser.dto.response.SendMetaResponse.ParticipantsInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgoraUserRepository extends JpaRepository<AgoraUser, Integer>, AgoraUserQueryRepository {

    boolean existsByAgoraIdAndUserId(Long agoraId, Long userId);

    Optional<AgoraUser> findByAgoraIdAndUserId(Long agoraId, Long userId);

    @Query("SELECT new com.attica.athens.domain.agoraUser.dto.response.SendMetaResponse$ParticipantsInfo(au.type, COUNT(au)) "
            +
            "FROM AgoraUser au " +
            "WHERE au.agora.id = :agoraId " +
            "AND au.sessionId IS NOT NULL " +
            "GROUP BY au.type")
    List<ParticipantsInfo> countActiveAgoraUsersByType(@Param("agoraId") Long agoraId);

    List<AgoraUser> findByAgoraId(Long agoraId);

    List<AgoraUser> findByAgoraIdAndTypeIn(Long agoraId, List<AgoraUserType> types);

    int countByAgoraId(Long agoraId);

    Optional<AgoraUser> findBySessionId(String sessionName);
}
