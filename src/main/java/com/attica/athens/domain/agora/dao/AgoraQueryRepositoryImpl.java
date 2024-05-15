package com.attica.athens.domain.agora.dao;

import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraUser.domain.QAgoraUser.agoraUser;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.SimpleParticipants;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AgoraQueryRepositoryImpl implements AgoraQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public AgoraQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(Long agoraId, AgoraStatus status, String keyword) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
            .select(Projections.constructor(
                SimpleAgoraResult.class,
                agora.id,
                agora.title,
                agora.color,
                Projections.constructor(SimpleParticipants.class,
                    new CaseBuilder()
                        .when(agoraUser.type.eq(AgoraUserType.PROS)).then(agoraUser.count())
                        .otherwise(0L)
                        .intValue(),
                    new CaseBuilder()
                        .when(agoraUser.type.eq(AgoraUserType.CONS)).then(agoraUser.count())
                        .otherwise(0L)
                        .intValue(),
                    new CaseBuilder()
                        .when(agoraUser.type.eq(AgoraUserType.OBSERVER)).then(agoraUser.count())
                        .otherwise(0L)
                        .intValue()
                ),
                agora.createdAt,
                agora.status
            ))
            .from(agora)
            .leftJoin(agora.agoraUsers, agoraUser)
            .where(gtAgoraId(agoraId),
                containKeyword(keyword),
                agora.status.eq(status)
            )
            .groupBy(agora.id, agoraUser.type)
            .orderBy(agora.id.desc())
            .limit(size + 1L)
            .fetch();

        return getSimpleAgoraResultAgoraSlice(size, result);
    }

    @Override
    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(Long agoraId, AgoraStatus status, List<String> categories) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
            .select(Projections.constructor(
                SimpleAgoraResult.class,
                agora.id,
                agora.title,
                agora.color,
                Projections.constructor(SimpleParticipants.class,
                    new CaseBuilder()
                        .when(agoraUser.type.eq(AgoraUserType.PROS)).then(agoraUser.count())
                        .otherwise(0L)
                        .intValue(),
                    new CaseBuilder()
                        .when(agoraUser.type.eq(AgoraUserType.CONS)).then(agoraUser.count())
                        .otherwise(0L)
                        .intValue(),
                    new CaseBuilder()
                        .when(agoraUser.type.eq(AgoraUserType.OBSERVER)).then(agoraUser.count())
                        .otherwise(0L)
                        .intValue()
                ),
                agora.createdAt,
                agora.status
            ))
            .from(agora)
            .leftJoin(agora.agoraUsers, agoraUser)
            .where(gtAgoraId(agoraId),
                agora.status.eq(status)
                .and(agora.code.code.in(categories))
            )
            .groupBy(agora.id, agoraUser.type)
            .orderBy(agora.id.desc())
            .limit(size + 1L)
            .fetch();

        return getSimpleAgoraResultAgoraSlice(size, result);
    }

    private AgoraSlice<SimpleAgoraResult> getSimpleAgoraResultAgoraSlice(final int size, final List<SimpleAgoraResult> result) {
        boolean hasNext = false;
        Long lastAgoraId = null;
        if (result != null && result.size() > size) {
            result.remove(size);
            lastAgoraId = result.get(result.size() - 1).id();
            hasNext = true;
        }

        return new AgoraSlice<>(result, lastAgoraId, hasNext);
    }

    private BooleanExpression containKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        return agora.title.containsIgnoreCase(keyword);
    }
    private BooleanExpression gtAgoraId(Long agoraId) {
        if (agoraId == null) return null;
        return agora.id.lt(agoraId);
    }
}
