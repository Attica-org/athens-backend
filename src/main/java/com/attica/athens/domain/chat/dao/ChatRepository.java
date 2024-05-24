package com.attica.athens.domain.chat.dao;


import com.attica.athens.domain.chat.domain.Chat;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByAgoraUserIdInOrderByIdDesc(List<Long> agoraUserIds, Pageable pageable);

    List<Chat> findByAgoraUserIdInAndIdLessThanOrderByIdDesc(List<Long> agoraUserIds, Long id, Pageable pageable);
}
