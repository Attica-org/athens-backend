package com.attica.athens.domain.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1567954940L;

    public static final QMember member = new QMember("member1");

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

    public final StringPath password = createString("password");

    //inherited
    public final EnumPath<MemberRole> role = _super.role;

    public final StringPath username = createString("username");

    //inherited
    public final ComparablePath<java.util.UUID> uuid = _super.uuid;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

