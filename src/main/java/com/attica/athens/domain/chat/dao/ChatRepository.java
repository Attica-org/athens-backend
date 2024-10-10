package com.attica.athens.domain.chat.dao;


import com.attica.athens.domain.chat.domain.Chat;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.agoraMember.id IN :agoraMemberIds " +
            "AND (:cursorId IS NULL OR c.id < :cursorId) " +
            "ORDER BY c.id DESC")
    List<Chat> findChatsForAgoraMembers(
            @Param("agoraMemberIds") List<Long> agoraMemberIds,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("SELECT c FROM Chat c WHERE c.agoraMember.id IN :agoraMemberIds " +
            "AND c.createdAt >= :joinTime " +
            "AND (:cursorId IS NULL OR c.id < :cursorId) " +
            "ORDER BY c.id DESC")
    List<Chat> findChatsFromJoinTime(
            @Param("agoraMemberIds") List<Long> agoraMemberIds,
            @Param("joinTime") LocalDateTime joinTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
