package com.attica.athens.domain.chat.dao;


import com.attica.athens.domain.chat.domain.Reaction;
import com.attica.athens.domain.chat.domain.ReactionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Query("SELECT r.type, COUNT(r) FROM Reaction r WHERE r.chat.id = :chatId GROUP BY r.type")
    List<Object[]> countReactionsByChatId(@Param("chatId") Long chatId);

    boolean existsByChatIdAndAgoraMemberIdAndType(Long chatId, Long agoraMemberId, ReactionType type);

    void deleteByChatIdAndAgoraMemberIdAndType(Long chatId, Long agoraMemberId, ReactionType type);
}
