package com.attica.athens.domain.agoraMember.dao;


import static com.attica.athens.domain.agora.domain.QAgora.agora;
import static com.attica.athens.domain.agoraMember.domain.QAgoraMember.agoraMember;

import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgoraMemberQueryRepositoryImpl implements AgoraMemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsNickname(Long agoraId, String nickname) {
        return jpaQueryFactory
                .selectFrom(agoraMember)
                .where(agoraMember.agora.id.eq(agoraId)
                        .and(agoraMember.nickname.lower().eq(nickname.toLowerCase()))
                )
                .fetchOne() != null;
    }

    @Override
    public int countCapacityByAgoraMemberType(Long agoraId, AgoraMemberType type) {
        return Objects.requireNonNull(jpaQueryFactory
                        .select(agoraMember.count())
                        .from(agoraMember)
                        .where(agoraMember.type.eq(type)
                                .and(agora.id.eq(agoraId))
                                .and(agoraMember.disconnectType.isFalse())
                                .and(agoraMember.socketDisconnectTime.isNull()))
                        .fetchOne())
                .intValue();
    }
}
