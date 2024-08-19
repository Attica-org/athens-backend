package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.common.AuditingFields;
import com.attica.athens.global.auth.exception.NullFieldException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "reactions", indexes = {
        @Index(name = "idx_reaction_chat", columnList = "chat_id"),
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reaction extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReactionType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agora_member_id")
    private AgoraMember agoraMember;

    public Reaction(ReactionType type, Chat chat, AgoraMember agoraMember) {
        validateType(type);
        validateChat(chat);
        validateAgoraMember(agoraMember);

        this.type = type;
        this.chat = chat;
        this.agoraMember = agoraMember;
    }

    private void validateType(ReactionType type) {
        if (type == null) {
            throw new NullFieldException("type");
        }
    }

    private void validateChat(Chat chat) {
        if (chat == null) {
            throw new NullFieldException("chat");
        }
    }

    private void validateAgoraMember(AgoraMember agoraMember) {
        if (agoraMember == null) {
            throw new NullFieldException("agoraMember");
        }
    }
}
