package com.attica.athens.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBaseUser is a Querydsl query type for BaseUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBaseUser extends EntityPathBase<BaseUser> {

    private static final long serialVersionUID = -2011443889L;

    public static final QBaseUser baseUser = new QBaseUser("baseUser");

    public final com.attica.athens.domain.common.QAuditingFields _super = new com.attica.athens.domain.common.QAuditingFields(this);

    public final ListPath<com.attica.athens.domain.agoraUser.domain.AgoraUser, com.attica.athens.domain.agoraUser.domain.QAgoraUser> agoraUsers = this.<com.attica.athens.domain.agoraUser.domain.AgoraUser, com.attica.athens.domain.agoraUser.domain.QAgoraUser>createList("agoraUsers", com.attica.athens.domain.agoraUser.domain.AgoraUser.class, com.attica.athens.domain.agoraUser.domain.QAgoraUser.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    public final ComparablePath<java.util.UUID> uuid = createComparable("uuid", java.util.UUID.class);

    public QBaseUser(String variable) {
        super(BaseUser.class, forVariable(variable));
    }

    public QBaseUser(Path<? extends BaseUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseUser(PathMetadata metadata) {
        super(BaseUser.class, metadata);
    }

}

