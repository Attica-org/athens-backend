package com.attica.athens.domain.agora.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrend is a Querydsl query type for Trend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrend extends EntityPathBase<Trend> {

    private static final long serialVersionUID = -1180736203L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrend trend = new QTrend("trend");

    public final QAgora agora;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> score = createNumber("score", Double.class);

    public QTrend(String variable) {
        this(Trend.class, forVariable(variable), INITS);
    }

    public QTrend(Path<? extends Trend> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrend(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrend(PathMetadata metadata, PathInits inits) {
        this(Trend.class, metadata, inits);
    }

    public QTrend(Class<? extends Trend> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agora = inits.isInitialized("agora") ? new QAgora(forProperty("agora"), inits.get("agora")) : null;
    }

}

