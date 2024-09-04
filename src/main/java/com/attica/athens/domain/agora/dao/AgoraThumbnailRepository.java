package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.AgoraThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgoraThumbnailRepository extends JpaRepository<AgoraThumbnail, Long> {

    AgoraThumbnail findAgoraThumbnailByOriginName(String originName);
}
