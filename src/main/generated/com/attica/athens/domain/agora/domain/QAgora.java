package com.attica.athens.domain.agora.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgora is a Querydsl query type for Agora
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgora extends EntityPathBase<Agora> {

    private static final long serialVersionUID = -1198601072L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgora agora = new QAgora("agora");

    public final com.attica.athens.domain.common.QAuditingFields _super = new com.attica.athens.domain.common.QAuditingFields(this);

    public final ListPath<com.attica.athens.domain.agoraUser.domain.AgoraUser, com.attica.athens.domain.agoraUser.domain.QAgoraUser> agoraUsers = this.<com.attica.athens.domain.agoraUser.domain.AgoraUser, com.attica.athens.domain.agoraUser.domain.QAgoraUser>createList("agoraUsers", com.attica.athens.domain.agoraUser.domain.AgoraUser.class, com.attica.athens.domain.agoraUser.domain.QAgoraUser.class, PathInits.DIRECT2);

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final QCategory category;

    public final StringPath color = createString("color");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Integer> duration = createNumber("duration", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final EnumPath<AgoraStatus> status = createEnum("status", AgoraStatus.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QAgora(String variable) {
        this(Agora.class, forVariable(variable), INITS);
    }

    public QAgora(Path<? extends Agora> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgora(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgora(PathMetadata metadata, PathInits inits) {
        this(Agora.class, metadata, inits);
    }

    public QAgora(Class<? extends Agora> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category"), inits.get("category")) : null;
    }

}

