package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Trend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PopularRepository extends JpaRepository<Trend, Long> {

    @Query("select p.agora.id from Trend p")
    List<Long> findAllIdsByPopular();
}
