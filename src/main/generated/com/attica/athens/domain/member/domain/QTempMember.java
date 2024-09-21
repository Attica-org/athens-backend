package com.attica.athens.domain.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTempMember is a Querydsl query type for TempMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTempMember extends EntityPathBase<TempMember> {

    private static final long serialVersionUID = 1443993584L;

    public static final QTempMember tempMember = new QTempMember("tempMember");

    public final QBaseMember _super = new QBaseMember(this);

    //inherited
    public final ListPath<com.attica.athens.domain.agoraMember.domain.AgoraMember, com.attica.athens.domain.agoraMember.domain.QAgoraMember> agoraMembers = _super.agoraMembers;

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
    public final EnumPath<MemberRole> role = _super.role;

    //inherited
    public final ComparablePath<java.util.UUID> uuid = _super.uuid;

    public QTempMember(String variable) {
        super(TempMember.class, forVariable(variable));
    }

    public QTempMember(Path<? extends TempMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTempMember(PathMetadata metadata) {
        super(TempMember.class, metadata);
    }

}

