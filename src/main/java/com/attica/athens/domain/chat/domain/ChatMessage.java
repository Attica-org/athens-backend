package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.common.AuditingFields;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(name = "idx_chat_message_created_at", columnList = "createdAt")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private MessageType type;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agora_user_id")
    private AgoraUser user;

    private ChatMessage(MessageType type, String content, AgoraUser user) {
        this.type = type;
        this.content = content;
        this.user = user;
    }

    public static ChatMessage createChatMessage(MessageType type, String content, AgoraUser user) {
        return new ChatMessage(type, content, user);
    }
}
