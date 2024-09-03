package com.attica.athens.domain.chat.dao;


import com.attica.athens.domain.chat.domain.Reaction;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.projection.ReactionCount;
import com.attica.athens.domain.chat.dto.projection.ReactionCountById;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Query("SELECT r.type as type, COUNT(r) as count FROM Reaction r WHERE r.chat.id = :chatId GROUP BY r.type")
    List<ReactionCount> countReactionsByChatId(@Param("chatId") Long chatId);

    @Query("SELECT r.chat.id as chatId, r.type as type, COUNT(r) as count " +
            "FROM Reaction r " +
            "WHERE r.chat.id IN :chatIds " +
            "GROUP BY r.chat.id, r.type")
    List<ReactionCountById> countReactionsByChatIds(@Param("chatIds") List<Long> chatIds);

    boolean existsByChatIdAndAgoraMemberIdAndType(Long chatId, Long agoraMemberId, ReactionType type);

    void deleteByChatIdAndAgoraMemberIdAndType(Long chatId, Long agoraMemberId, ReactionType type);
}
