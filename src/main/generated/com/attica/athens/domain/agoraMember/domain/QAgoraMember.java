package com.attica.athens.domain.agoraMember.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgoraMember is a Querydsl query type for AgoraMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgoraMember extends EntityPathBase<AgoraMember> {

    private static final long serialVersionUID = -1894323376L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgoraMember agoraMember = new QAgoraMember("agoraMember");

    public final com.attica.athens.domain.common.QAuditingFields _super = new com.attica.athens.domain.common.QAuditingFields(this);

    public final com.attica.athens.domain.agora.domain.QAgora agora;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final BooleanPath endVoted = createBoolean("endVoted");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isOpinionVoted = createBoolean("isOpinionVoted");

    public final com.attica.athens.domain.member.domain.QBaseMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> photoNumber = createNumber("photoNumber", Integer.class);

    public final StringPath sessionId = createString("sessionId");

    public final EnumPath<AgoraMemberType> type = createEnum("type", AgoraMemberType.class);

    public final EnumPath<AgoraVoteType> voteType = createEnum("voteType", AgoraVoteType.class);

    public QAgoraMember(String variable) {
        this(AgoraMember.class, forVariable(variable), INITS);
    }

    public QAgoraMember(Path<? extends AgoraMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgoraMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgoraMember(PathMetadata metadata, PathInits inits) {
        this(AgoraMember.class, metadata, inits);
    }

    public QAgoraMember(Class<? extends AgoraMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agora = inits.isInitialized("agora") ? new com.attica.athens.domain.agora.domain.QAgora(forProperty("agora"), inits.get("agora")) : null;
        this.member = inits.isInitialized("member") ? new com.attica.athens.domain.member.domain.QBaseMember(forProperty("member")) : null;
    }

}

