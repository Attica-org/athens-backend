package com.attica.athens.domain.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBaseMember is a Querydsl query type for BaseMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBaseMember extends EntityPathBase<BaseMember> {

    private static final long serialVersionUID = 20474157L;

    public static final QBaseMember baseMember = new QBaseMember("baseMember");

    public final com.attica.athens.domain.common.QAuditingFields _super = new com.attica.athens.domain.common.QAuditingFields(this);

    public final ListPath<com.attica.athens.domain.agoraMember.domain.AgoraMember, com.attica.athens.domain.agoraMember.domain.QAgoraMember> agoraMembers = this.<com.attica.athens.domain.agoraMember.domain.AgoraMember, com.attica.athens.domain.agoraMember.domain.QAgoraMember>createList("agoraMembers", com.attica.athens.domain.agoraMember.domain.AgoraMember.class, com.attica.athens.domain.agoraMember.domain.QAgoraMember.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final EnumPath<MemberRole> role = createEnum("role", MemberRole.class);

    public final ComparablePath<java.util.UUID> uuid = createComparable("uuid", java.util.UUID.class);

    public QBaseMember(String variable) {
        super(BaseMember.class, forVariable(variable));
    }

    public QBaseMember(Path<? extends BaseMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseMember(PathMetadata metadata) {
        super(BaseMember.class, metadata);
    }

}

