package com.attica.athens.domain.agora.dao;

import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraUser.domain.QAgoraUser.agoraUser;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.SimpleParticipants;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.exception.NotFoundAgoraIdException;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraQueryRepositoryImpl implements AgoraQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(Long agoraId, List<AgoraStatus> status, String keyword) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleAgoraResult.class,
                        agora.id,
                        agora.title,
                        agora.color,
                        Projections.constructor(SimpleParticipants.class,
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.PROS))),
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.CONS))),
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.OBSERVER)))
                        ),
                        agora.createdAt,
                        agora.status
                ))
                .from(agora)
                .where(gtAgoraId(agoraId),
                        (containKeyword(keyword))
                                .and((agora.status.in(status)))
                )
                .orderBy(agora.id.desc())
                .limit(size + 1L)
                .fetch();

        return getSimpleAgoraResultAgoraSlice(size, result);
    }

    @Override
    public AgoraSlice<SimpleAgoraResult> findAgoraByCategory(Long agoraId, List<AgoraStatus> status,
                                                             List<Long> categoryIds) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleAgoraResult.class,
                        agora.id,
                        agora.title,
                        agora.color,
                        Projections.constructor(SimpleParticipants.class,
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.PROS))),
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.CONS))),
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.OBSERVER)))
                        ),
                        agora.createdAt,
                        agora.status
                ))
                .from(agora)
                .where(gtAgoraId(agoraId),
                        agora.status.in(status)
                                .and(agora.category.id.in(categoryIds))
                )
                .orderBy(agora.id.desc())
                .limit(size + 1L)
                .fetch();

        return getSimpleAgoraResultAgoraSlice(size, result);
    }

    @Override
    public AgoraSlice<SimpleAgoraResult> findAgoraByAllCategory(Long agoraId, List<AgoraStatus> status) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleAgoraResult.class,
                        agora.id,
                        agora.title,
                        agora.color,
                        Projections.constructor(SimpleParticipants.class,
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.PROS))),
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.CONS))),
                                JPAExpressions.select(agoraUser.count())
                                        .from(agoraUser)
                                        .where(agoraUser.agora.id.eq(agora.id)
                                                .and(agoraUser.type.eq(AgoraUserType.OBSERVER)))
                        ),
                        agora.createdAt,
                        agora.status
                ))
                .from(agora)
                .where(gtAgoraId(agoraId), agora.status.in(status))
                .orderBy(agora.id.desc())
                .limit(size + 1L)
                .fetch();

        return getSimpleAgoraResultAgoraSlice(size, result);
    }

    @Override
    public List<Long> getAgoraIdList() {

        final int size = 30;

        List<Long> agoraIdList = jpaQueryFactory
                .select(agora.id)
                .from(agora)
                .limit(size + 1L)
                .fetch();

        if (agoraIdList.isEmpty()) {
            throw new NotFoundAgoraIdException();
        }

        return agoraIdList;
    }

    @Override
    public Optional<Agora> findAgoraById(Long agoraId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(agora)
                .where(agora.id.eq(agoraId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne()
        );
    }

    private AgoraSlice<SimpleAgoraResult> getSimpleAgoraResultAgoraSlice(final int size,
                                                                         final List<SimpleAgoraResult> result) {
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
        // if (keyword == null || keyword.isEmpty()) return null;
        return agora.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression gtAgoraId(Long agoraId) {
        if (agoraId == null) {
            return null;
        }
        return agora.id.lt(agoraId);
    }
}
