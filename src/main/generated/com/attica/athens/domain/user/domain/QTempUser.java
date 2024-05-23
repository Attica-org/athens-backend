package com.attica.athens.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTempUser is a Querydsl query type for TempUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTempUser extends EntityPathBase<TempUser> {

    private static final long serialVersionUID = -1612197678L;

    public static final QTempUser tempUser = new QTempUser("tempUser");

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

    //inherited
    public final EnumPath<UserRole> role = _super.role;

    //inherited
    public final ComparablePath<java.util.UUID> uuid = _super.uuid;

    public QTempUser(String variable) {
        super(TempUser.class, forVariable(variable));
    }

    public QTempUser(Path<? extends TempUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTempUser(PathMetadata metadata) {
        super(TempUser.class, metadata);
    }

}

