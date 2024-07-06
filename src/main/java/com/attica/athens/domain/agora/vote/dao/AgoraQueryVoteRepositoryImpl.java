package com.attica.athens.domain.agora.vote.dao;


import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraMember.domain.QAgoraMember.agoraMember;

import com.attica.athens.domain.agoraMember.domain.AgoraVoteType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraQueryVoteRepositoryImpl implements AgoraQueryVoteRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer getProsVoteResult(Long agoraId) {
        List<Long> prosResult = queryFactory.select(agoraMember.voteType.count())
                .from(agoraMember)
                .where(agora.id.eq(agoraId).and(agoraMember.voteType.eq(AgoraVoteType.PROS)))
                .groupBy(agoraMember.voteType)
                .fetch();

        return prosResult.isEmpty() ? 0 : prosResult.get(0).intValue();
    }

    @Override
    public Integer getConsVoteResult(Long agoraId) {
        List<Long> consResult = queryFactory.select(agoraMember.voteType.count())
                .from(agoraMember)
                .where(agora.id.eq(agoraId).and(agoraMember.voteType.eq(AgoraVoteType.CONS)))
                .groupBy(agoraMember.voteType)
                .fetch();

        return consResult.isEmpty() ? 0 : consResult.get(0).intValue();
    }
}
