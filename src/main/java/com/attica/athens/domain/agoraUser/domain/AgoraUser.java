package com.attica.athens.domain.agoraUser.domain;

import com.attica.athens.domain.agora.domain.Agora;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgoraUser extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private AgoraUserType type;

    @Column(length = 25, nullable = false)
    private String nickname;

    @Column(name = "photo_number", nullable = false)
    private Integer photoNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id")
    private Agora agora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BaseUser user;

    private AgoraUser(AgoraUserType type, String nickname, Integer photoNumber, Agora agora, BaseUser user) {
        this.type = type;
        this.nickname = nickname;
        this.photoNumber = photoNumber;
        this.agora = agora;
        this.user = user;
    }

    public static AgoraUser createAgoraUser(AgoraUserType type, String nickname, Integer photoNumber, Agora agora,
                                            BaseUser user) {
        return new AgoraUser(type, nickname, photoNumber, agora, user);
    }
}
