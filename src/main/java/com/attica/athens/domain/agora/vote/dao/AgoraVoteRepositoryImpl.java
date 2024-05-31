package com.attica.athens.domain.agora.vote.dao;

import static com.attica.athens.domain.agoraUser.domain.QAgoraUser.agoraUser;

import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AgoraVoteRepositoryImpl implements AgoraVoteRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public AgoraUser updateVoteType(AgoraUser user, AgoraVoteRequest agoraVoteRequest) {

        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, agoraUser);

        jpaUpdateClause
                .where(agoraUser.id.eq(user.getId()))
                .set(agoraUser.voteType, agoraVoteRequest.voteType())
                .execute();

        AgoraUser updatedAgoraUser = em.find(AgoraUser.class, user.getId());

        return updatedAgoraUser;
    }

}
