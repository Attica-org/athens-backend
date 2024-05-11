package com.attica.athens.agora.repository;

import com.attica.athens.agora.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String>, CategoryQueryRepository {
}
