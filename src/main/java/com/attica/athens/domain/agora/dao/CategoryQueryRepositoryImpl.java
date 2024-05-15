package com.attica.athens.domain.agora.dao;

import static com.attica.athens.domain.agora.domain.QCategory.category;

import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.domain.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<String> findParentCodeByCategory(String categoryId) {
        List<String> parentCodes = new ArrayList<>();
        String currentCategory = categoryId;

        while (currentCategory != null) {
            parentCodes.add(currentCategory);
            Category entity = jpaQueryFactory.selectFrom(category)
                .where(category.code.eq(currentCategory))
                .fetchOne();

            if (entity == null || entity.getParentCode() == null) {
                break;
            }

            currentCategory = entity.getParentCode().getCode();
        }

        return parentCodes;
    }
}
