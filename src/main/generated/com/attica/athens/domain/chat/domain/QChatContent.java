package com.attica.athens.domain.chat.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatContent is a Querydsl query type for ChatContent
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QChatContent extends BeanPath<ChatContent> {

    private static final long serialVersionUID = -423404191L;

    public static final QChatContent chatContent = new QChatContent("chatContent");

    public final StringPath content = createString("content");

    public QChatContent(String variable) {
        super(ChatContent.class, forVariable(variable));
    }

    public QChatContent(Path<? extends ChatContent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatContent(PathMetadata metadata) {
        super(ChatContent.class, metadata);
    }

}

