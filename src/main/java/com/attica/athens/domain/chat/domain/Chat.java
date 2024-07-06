package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.common.AuditingFields;
import com.attica.athens.global.auth.exception.NullFieldException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(name = "idx_chat_created_at", columnList = "createdAt")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ChatType type;

    @Embedded
    private ChatContent content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agora_member_id")
    private AgoraMember agoraMember;

    public Chat(ChatType type, ChatContent content, AgoraMember agoraMember) {
        validateType(type);
        validateContent(content);
        validateAgoraMember(agoraMember);

        this.type = type;
        this.content = content;
        this.agoraMember = agoraMember;
    }

    private void validateType(ChatType type) {
        if (type == null) {
            throw new NullFieldException("type");
        }
    }

    private void validateContent(ChatContent content) {
        if (content == null) {
            throw new NullFieldException("content");
        }
    }

    private void validateAgoraMember(AgoraMember agoraMember) {
        if (agoraMember == null) {
            throw new NullFieldException("agoraMember");
        }
    }
}
