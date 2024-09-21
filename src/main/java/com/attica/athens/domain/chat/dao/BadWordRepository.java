package com.attica.athens.domain.chat.dao;

import com.attica.athens.domain.chat.domain.BadWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadWordRepository extends JpaRepository<BadWord,Long> {
}
