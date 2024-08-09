package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Popular;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PopularRepository extends JpaRepository<Popular, Long> {

    @Query("select p.agora.id from Popular p order by p.score desc")
    List<Long> findAllIdsByPopular();
}
