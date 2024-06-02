package com.attica.athens.domain.agora.vote.dao;

import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraUser.domain.QAgoraUser.agoraUser;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
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
    public AgoraUser updateVoteType(AgoraUser user, AgoraVoteRequest agoraVoteRequest) {

        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, agoraUser);

        jpaUpdateClause
                .where(agoraUser.id.eq(user.getId()))
                .set(agoraUser.voteType, agoraVoteRequest.voteType())
                .execute();

        em.flush();
        em.clear();

        AgoraUser updatedAgoraUser = em.find(AgoraUser.class, user.getId());

        return updatedAgoraUser;
    }

    @Override
    public void updateVoteResult(Agora agoraResult, Long prosVoteResult, Long consVoteResult) {
        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, agora);

        jpaUpdateClause
                .where(agora.id.eq(agoraResult.getId()))
                .set(agora.prosCount, prosVoteResult)
                .set(agora.consCount, consVoteResult)
                .execute();

    }

    @Override
    public Long getProsVoteResult(Long agoraid) {
        List<Long> prosResult = queryFactory.select(agoraUser.voteType.count())
                .from(agoraUser)
                .where(agora.id.eq(agoraid).and(agoraUser.voteType.eq(AgoraVoteType.PROS)))
                .groupBy(agoraUser.voteType)
                .fetch();

        if(prosResult.isEmpty()) return 0L;

        return prosResult.get(0);
    }

    @Override
    public Long getConsVoteResult(Long agoraid) {
        List<Long> consResult = queryFactory.select(agoraUser.voteType.count())
                .from(agoraUser)
                .where(agora.id.eq(agoraid).and(agoraUser.voteType.eq(AgoraVoteType.CONS)))
                .groupBy(agoraUser.voteType)
                .fetch();

        if(consResult.isEmpty()) return 0L;

        return consResult.get(0);
    }

}
