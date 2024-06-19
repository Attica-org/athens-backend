package com.attica.athens.domain.agoraUser.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgoraUser is a Querydsl query type for AgoraUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgoraUser extends EntityPathBase<AgoraUser> {

    private static final long serialVersionUID = 1568620016L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgoraUser agoraUser = new QAgoraUser("agoraUser");

    public final com.attica.athens.domain.common.QAuditingFields _super = new com.attica.athens.domain.common.QAuditingFields(this);

    public final com.attica.athens.domain.agora.domain.QAgora agora;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final BooleanPath endVoted = createBoolean("endVoted");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isOpinionVoted = createBoolean("isOpinionVoted");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> photoNumber = createNumber("photoNumber", Integer.class);

    public final StringPath sessionId = createString("sessionId");

    public final EnumPath<AgoraUserType> type = createEnum("type", AgoraUserType.class);

    public final com.attica.athens.domain.user.domain.QBaseUser user;

    public final EnumPath<AgoraVoteType> voteType = createEnum("voteType", AgoraVoteType.class);

    public QAgoraUser(String variable) {
        this(AgoraUser.class, forVariable(variable), INITS);
    }

    public QAgoraUser(Path<? extends AgoraUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgoraUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgoraUser(PathMetadata metadata, PathInits inits) {
        this(AgoraUser.class, metadata, inits);
    }

    public QAgoraUser(Class<? extends AgoraUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agora = inits.isInitialized("agora") ? new com.attica.athens.domain.agora.domain.QAgora(forProperty("agora"), inits.get("agora")) : null;
        this.user = inits.isInitialized("user") ? new com.attica.athens.domain.user.domain.QBaseUser(forProperty("user")) : null;
    }

}

