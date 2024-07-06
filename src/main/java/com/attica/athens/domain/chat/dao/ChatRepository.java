package com.attica.athens.domain.chat.dao;


import com.attica.athens.domain.chat.domain.Chat;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.agoraUser.id IN :agoraUserIds " +
            "AND (:cursorId IS NULL OR c.id < :cursorId) " +
            "ORDER BY c.id DESC")
    List<Chat> findChatsForAgoraUsers(
            @Param("agoraUserIds") List<Long> agoraUserIds,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
