package com.attica.athens.domain.agoraMember.dao;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.attica.athens.domain.agoraMember.dto.response.SendMetaResponse.ParticipantsInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgoraMemberRepository extends JpaRepository<AgoraMember, Long>, AgoraMemberQueryRepository {

    boolean existsByAgoraIdAndMemberId(Long agoraId, Long memberId);

    Optional<AgoraMember> findByAgoraIdAndMemberId(Long agoraId, Long memberId);

    Optional<AgoraMember> findByAgoraIdAndMemberIdAndSessionIdIsNotNull(Long agoraId, Long userId);

    @Query("SELECT new com.attica.athens.domain.agoraMember.dto.response.SendMetaResponse$ParticipantsInfo(au.type, COUNT(au)) "
            +
            "FROM AgoraMember au " +
            "WHERE au.agora.id = :agoraId " +
            "AND au.sessionId IS NOT NULL " +
            "GROUP BY au.type")
    List<ParticipantsInfo> countActiveAgoraMembersByType(@Param("agoraId") Long agoraId);

    @Query("SELECT am FROM AgoraMember am WHERE am.agora.id = :agoraId AND am.member.id = :memberId ORDER BY am.createdAt DESC LIMIT 1")
    Optional<AgoraMember> findLatestByAgoraIdAndMemberId(@Param("agoraId") Long agoraId,
                                                         @Param("memberId") Long memberId);

    List<AgoraMember> findByAgoraId(Long agoraId);

    List<AgoraMember> findByAgoraIdAndTypeInAndSessionIdIsNotNull(Long agoraId, List<AgoraMemberType> types);

    int countByAgoraIdAndSessionIdIsNotNullAndTypeIsNot(Long agoraId, AgoraMemberType type);

    Optional<AgoraMember> findBySessionId(String sessionName);

    boolean existsByAgoraIdAndSessionIdIsNotNull(Long agoraId);

    Optional<AgoraMember> deleteByAgoraIdAndMemberId(Long agoraId, Long memberId);

    Optional<AgoraMember> findByMemberIdAndAgoraIdAndSocketDisconnectTimeIsNull(Long memberId, Long agoraId);
}
