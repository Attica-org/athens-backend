package com.attica.athens.agora.repository;

import com.attica.athens.agora.domain.Category;
import java.util.Optional;

public interface CategoryQueryRepository {

    Optional<Category> findCategoryByName(String name);
}
