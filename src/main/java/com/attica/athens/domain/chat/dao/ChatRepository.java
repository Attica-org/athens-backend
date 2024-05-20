package com.attica.athens.domain.chat.dao;


import com.attica.athens.domain.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
