package com.attica.athens.domain.agoraMember.domain;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.ObserverException;
import com.attica.athens.domain.common.AuditingFields;
import com.attica.athens.domain.member.domain.BaseMember;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(indexes = {
        @Index(name = "idx_agora_member_agora_member", columnList = "agora_id, member_id"),
        @Index(name = "idx_agora_member_session", columnList = "session_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgoraMember extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private AgoraMemberType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", length = 50, nullable = false)
    private AgoraVoteType voteType;

    @Column(length = 25)
    private String nickname;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "photo_number")
    private Integer photoNumber;

    @Column(name = "end_voted", nullable = false)
    private Boolean endVoted;

    @Column(name = "is_opinion_voted", nullable = false)
    private Boolean isOpinionVoted;

    @Column(name = "disconnect_type", nullable = false)
    private Boolean disconnectType;

    @Column(name = "socket_disconnect_time")
    private LocalDateTime socketDisconnectTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id")
    private Agora agora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private BaseMember member;

    @Builder
    public AgoraMember(AgoraMemberType type, String nickname, Integer photoNumber, Agora agora,
                       BaseMember member) {
        this.type = type;
        this.voteType = AgoraVoteType.DEFAULT;
        this.disconnectType = false;
        this.nickname = nickname;
        this.photoNumber = photoNumber;
        this.endVoted = false;
        this.isOpinionVoted = false;
        this.agora = agora;
        this.member = member;
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

    public void updateSocketDisconnectTime(LocalDateTime socketDisconnectTime) {
        this.socketDisconnectTime = socketDisconnectTime;
    }

    public void updateDisconnectType(Boolean disconnectType) {
        this.disconnectType = disconnectType;
    }

    public AgoraMember validateSendMessage() {
        if (!this.getType().canSendMessage()) {
            throw new ObserverException();
        }
        return this;
    }

    public void clearNickname() {
        this.nickname = null;
    }

    public void updateAgoraMember(String nickname, AgoraMemberType memberType) {
        updateNickname(nickname);
        updateMemberType(memberType);
    }

    private void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    private void updateMemberType(AgoraMemberType memberType) {
        this.type = memberType;
    }
}
