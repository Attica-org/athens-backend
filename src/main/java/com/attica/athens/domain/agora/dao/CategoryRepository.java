package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String>, CategoryQueryRepository {
}
