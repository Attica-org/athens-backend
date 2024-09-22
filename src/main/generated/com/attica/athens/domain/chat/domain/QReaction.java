package com.attica.athens.domain.chat.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReaction is a Querydsl query type for Reaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReaction extends EntityPathBase<Reaction> {

    private static final long serialVersionUID = 710606537L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReaction reaction = new QReaction("reaction");

    public final com.attica.athens.domain.common.QAuditingFields _super = new com.attica.athens.domain.common.QAuditingFields(this);

    public final com.attica.athens.domain.agoraMember.domain.QAgoraMember agoraMember;

    public final QChat chat;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final EnumPath<ReactionType> type = createEnum("type", ReactionType.class);

    public QReaction(String variable) {
        this(Reaction.class, forVariable(variable), INITS);
    }

    public QReaction(Path<? extends Reaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReaction(PathMetadata metadata, PathInits inits) {
        this(Reaction.class, metadata, inits);
    }

    public QReaction(Class<? extends Reaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agoraMember = inits.isInitialized("agoraMember") ? new com.attica.athens.domain.agoraMember.domain.QAgoraMember(forProperty("agoraMember"), inits.get("agoraMember")) : null;
        this.chat = inits.isInitialized("chat") ? new QChat(forProperty("chat"), inits.get("chat")) : null;
    }

}

