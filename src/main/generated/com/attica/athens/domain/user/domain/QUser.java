package com.attica.athens.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -613243810L;

    public static final QUser user = new QUser("user");

    public final QBaseUser _super = new QBaseUser(this);

    //inherited
    public final ListPath<com.attica.athens.domain.agoraUser.domain.AgoraUser, com.attica.athens.domain.agoraUser.domain.QAgoraUser> agoraUsers = _super.agoraUsers;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath password = createString("password");

    //inherited
    public final EnumPath<UserRole> role = _super.role;

    public final StringPath username = createString("username");

    //inherited
    public final ComparablePath<java.util.UUID> uuid = _super.uuid;

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

