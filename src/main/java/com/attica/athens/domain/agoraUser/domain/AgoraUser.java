package com.attica.athens.domain.agoraUser.domain;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.ObserverException;
import com.attica.athens.domain.common.AuditingFields;
import com.attica.athens.domain.user.domain.BaseUser;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(indexes = {
        @Index(name = "idx_agora_user_agora_user", columnList = "agora_id, user_id"),
        @Index(name = "idx_agora_user_session", columnList = "session_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgoraUser extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private AgoraUserType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private AgoraVoteType voteType;

    @Column(length = 25)
    private String nickname;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "photo_number")
    private Integer photoNumber;

    @Column(name = "end_voted", nullable = false, columnDefinition = "BIT default 0")
    private Boolean endVoted;

    @Column(name = "is_opinion_voted", nullable = false, columnDefinition = "BIT default 0")
    private Boolean isOpinionVoted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id")
    private Agora agora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BaseUser user;

    @Builder
    public AgoraUser(AgoraUserType type, String nickname, Integer photoNumber, Agora agora,
                     BaseUser user) {
        this.type = type;
        this.voteType = AgoraVoteType.DEFAULT;
        this.nickname = nickname;
        this.photoNumber = photoNumber;
        this.endVoted = false;
        this.isOpinionVoted = false;
        this.agora = agora;
        this.user = user;
    }

    public void markEndVoted() {
        this.endVoted = true;
    }

    public void updateIsOpinionVotedAndVoteType(AgoraVoteType voteType, boolean isOpinionVoted) {
        this.voteType = voteType;
        this.isOpinionVoted = isOpinionVoted;
    }

    public void updateSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public AgoraUser validateSendMessage() {
        if (!this.getType().canSendMessage()) {
            throw new ObserverException();
        }
        return this;
    }
}
