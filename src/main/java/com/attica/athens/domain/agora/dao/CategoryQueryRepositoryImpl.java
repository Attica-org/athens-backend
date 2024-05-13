package com.attica.athens.domain.agora.dao;

import static com.attica.athens.domain.agora.domain.QCategory.category;

import com.attica.athens.domain.agora.domain.Category;
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
    public Optional<Category> findCategoryByName(String code) {
        Category entity = jpaQueryFactory.selectFrom(category)
                .where(category.code.eq(code))
                .fetchOne();

        return Optional.ofNullable(entity);
    }
}
