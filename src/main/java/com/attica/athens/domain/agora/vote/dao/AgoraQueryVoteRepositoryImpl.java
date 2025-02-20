package com.attica.athens.domain.agora.vote.dao;


import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraMember.domain.QAgoraMember.agoraMember;

import com.attica.athens.domain.agora.vote.dto.response.VoteResultResponse;
import com.attica.athens.domain.agoraMember.domain.AgoraVoteType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraQueryVoteRepositoryImpl implements AgoraQueryVoteRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public VoteResultResponse getVoteResults(Long agoraId) {
        return queryFactory
                .select(Projections.constructor(
                        VoteResultResponse.class,
                        agoraMember.voteType.when(AgoraVoteType.PROS).then(1).otherwise(0).sum(),
                        agoraMember.voteType.when(AgoraVoteType.CONS).then(1).otherwise(0).sum()
                ))
                .from(agoraMember)
                .where(agora.id.eq(agoraId))
                .fetchOne();
    }
}
