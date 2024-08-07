package com.attica.athens.domain.agora.dao;

import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraMember.domain.QAgoraMember.agoraMember;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.SimpleClosedAgoraVoteResult;
import com.attica.athens.domain.agora.dto.SimpleParticipants;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.exception.NotFoundAgoraIdException;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraQueryRepositoryImpl implements AgoraQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public AgoraSlice<SimpleAgoraResult> findActiveAgoraByKeyword(Long agoraId, List<AgoraStatus> status,
                                                                  String keyword) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleAgoraResult.class,
                        agora.id,
                        agora.title,
                        agora.color,
                        Projections.constructor(SimpleParticipants.class,
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.PROS))
                                                .and(agoraMember.sessionId.isNotNull())),
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.CONS))
                                                .and(agoraMember.sessionId.isNotNull())),
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.OBSERVER))
                                                .and(agoraMember.sessionId.isNotNull()))
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

        return getAgoraResultSlice(size, result, SimpleAgoraResult::id);
    }

    @Override
    public AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraByKeyword(Long agoraId, List<AgoraStatus> status,
                                                                            String keyword) {
        final int size = 10;

        List<SimpleClosedAgoraVoteResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleClosedAgoraVoteResult.class,
                        agora.id,
                        agora.prosCount,
                        agora.consCount,
                        agora.title,
                        agora.color,
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

        return getAgoraResultSlice(size, result, SimpleClosedAgoraVoteResult::id);
    }

    @Override
    public AgoraSlice<SimpleAgoraResult> findActiveAgoraByCategory(Long agoraId, List<AgoraStatus> status,
                                                                   List<Long> categoryIds) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleAgoraResult.class,
                        agora.id,
                        agora.title,
                        agora.color,
                        Projections.constructor(SimpleParticipants.class,
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.PROS))
                                                .and(agoraMember.sessionId.isNotNull())),
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.CONS))
                                                .and(agoraMember.sessionId.isNotNull())),
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.OBSERVER))
                                                .and(agoraMember.sessionId.isNotNull()))
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

        return getAgoraResultSlice(size, result, SimpleAgoraResult::id);
    }

    @Override
    public AgoraSlice<SimpleAgoraResult> findActiveAgoraByAllCategory(Long agoraId, List<AgoraStatus> status) {
        final int size = 10;

        List<SimpleAgoraResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleAgoraResult.class,
                        agora.id,
                        agora.title,
                        agora.color,
                        Projections.constructor(SimpleParticipants.class,
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.PROS))
                                                .and(agoraMember.sessionId.isNotNull())),
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.CONS))
                                                .and(agoraMember.sessionId.isNotNull())),
                                JPAExpressions.select(agoraMember.count())
                                        .from(agoraMember)
                                        .where(agoraMember.agora.id.eq(agora.id)
                                                .and(agoraMember.type.eq(AgoraMemberType.OBSERVER))
                                                .and(agoraMember.sessionId.isNotNull()))
                        ),
                        agora.createdAt,
                        agora.status
                ))
                .from(agora)
                .where(gtAgoraId(agoraId), agora.status.in(status))
                .orderBy(agora.id.desc())
                .limit(size + 1L)
                .fetch();

        return getAgoraResultSlice(size, result, SimpleAgoraResult::id);
    }

    @Override
    public AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraVoteResultsByCategory(Long agoraId,
                                                                                        List<Long> categoryIds,
                                                                                        List<AgoraStatus> status) {

        final int size = 10;

        List<SimpleClosedAgoraVoteResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleClosedAgoraVoteResult.class,
                        agora.id,
                        agora.prosCount,
                        agora.consCount,
                        agora.title,
                        agora.color,
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

        return getAgoraResultSlice(size, result, SimpleClosedAgoraVoteResult::id);
    }

    @Override
    public AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraVoteResultsByAllCategory(Long agoraId,
                                                                                           List<AgoraStatus> status) {

        final int size = 10;

        List<SimpleClosedAgoraVoteResult> result = jpaQueryFactory
                .select(Projections.constructor(
                        SimpleClosedAgoraVoteResult.class,
                        agora.id,
                        agora.prosCount,
                        agora.consCount,
                        agora.title,
                        agora.color,
                        agora.createdAt,
                        agora.status
                ))
                .from(agora)
                .where(gtAgoraId(agoraId),
                        agora.status.in(status)
                )
                .orderBy(agora.id.desc())
                .limit(size + 1L)
                .fetch();

        return getAgoraResultSlice(size, result, SimpleClosedAgoraVoteResult::id);
    }

    @Override
    public List<Long> getAgoraIdList() {

        final int size = 30;

        List<Long> agoraIdList = jpaQueryFactory
                .select(agora.id)
                .from(agora)
                .orderBy(agora.id.asc())
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

    private <T> AgoraSlice<T> getAgoraResultSlice(final int size, final List<T> result, Function<T, Long> idExtractor) {
        boolean hasNext = false;
        Long lastAgoraId = null;
        if (result != null && result.size() > size) {
            result.remove(size);
            lastAgoraId = idExtractor.apply(result.get(result.size() - 1));
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
