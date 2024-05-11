package com.attica.athens.agora.repository;

import static com.attica.athens.agora.domain.QCategory.category;

import com.attica.athens.agora.domain.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CategoryQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.jpaQueryFactory = queryFactory;
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        Category entity = jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(name))
            .fetchOne();

        return Optional.ofNullable(entity);
    }
}
