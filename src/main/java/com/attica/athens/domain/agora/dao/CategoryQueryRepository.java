package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Category;
import java.util.Optional;

public interface CategoryQueryRepository {

    Optional<Category> findCategoryByName(String name);
}
