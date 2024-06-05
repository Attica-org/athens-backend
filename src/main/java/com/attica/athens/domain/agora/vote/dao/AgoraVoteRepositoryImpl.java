package com.attica.athens.domain.agora.vote.dao;

import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraUser.domain.QAgoraUser.agoraUser;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraVoteRepositoryImpl implements AgoraVoteRepository {

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public AgoraUser updateVoteType(Long userId, AgoraVoteType voteType, Boolean opinionVoted, Long agoraId) {

        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, agoraUser);

        jpaUpdateClause
                .where(agora.id.eq(agoraId).and(agoraUser.id.eq(userId)))
                .set(agoraUser.voteType, voteType)
                .set(agoraUser.opinionVoted, opinionVoted)
                .execute();

        em.flush();
        em.clear();

        AgoraUser updatedAgoraUser = em.find(AgoraUser.class, userId);

        return updatedAgoraUser;
    }

    @Override
    public void updateVoteResult(Long agoraId, Integer prosVoteResult, Integer consVoteResult) {
        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, agora);

        jpaUpdateClause
                .where(agora.id.eq(agoraId))
                .set(agora.prosCount, prosVoteResult)
                .set(agora.consCount, consVoteResult)
                .execute();

    }

    @Override
    public Integer getProsVoteResult(Long agoraid) {
        List<Long> prosResult = queryFactory.select(agoraUser.voteType.count())
                .from(agoraUser)
                .where(agora.id.eq(agoraid).and(agoraUser.voteType.eq(AgoraVoteType.PROS)))
                .groupBy(agoraUser.voteType)
                .fetch();

        return prosResult.isEmpty() ? 0 : prosResult.get(0).intValue();
    }

    @Override
    public Integer getConsVoteResult(Long agoraid) {
        List<Long> consResult = queryFactory.select(agoraUser.voteType.count())
                .from(agoraUser)
                .where(agora.id.eq(agoraid).and(agoraUser.voteType.eq(AgoraVoteType.CONS)))
                .groupBy(agoraUser.voteType)
                .fetch();

        return consResult.isEmpty() ? 0 : consResult.get(0).intValue();
    }
}
