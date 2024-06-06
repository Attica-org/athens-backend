package com.attica.athens.domain.agora.vote.dao;

import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraUser.domain.QAgoraUser.agoraUser;

import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraQueryVoteRepositoryImpl implements AgoraQueryVoteRepository {

    private final JPAQueryFactory queryFactory;

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
