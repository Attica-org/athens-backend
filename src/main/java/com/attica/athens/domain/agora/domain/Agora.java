package com.attica.athens.domain.agora.domain;

import static com.attica.athens.domain.agora.domain.AgoraConstants.END_VOTE_RATIO;

import com.attica.athens.domain.agora.exception.InvalidAgoraStatusChangeException;
import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.exception.InvalidAgoraVoteTypeException;
import com.attica.athens.domain.agora.vote.exception.VoteTimeOutException;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.common.AuditingFields;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@ToString(callSuper = true)
@Table(
        name = "agora",
        indexes = {
                @Index(name = "idx_agora_title", columnList = "title"),
                @Index(name = "idx_agora_created_at", columnList = "createdAt"),
                @Index(name = "idx_agora_created_by", columnList = "createdBy")
        })
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agora extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_id")
    private Long id;
    @Column(length = 50, nullable = false)
    private String title;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private Integer duration;
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;
    @Column(nullable = false)
    private String color;
    @Column(name = "pros_count", nullable = false)
    private Integer prosCount;
    @Column(name = "cons_count", nullable = false)
    private Integer consCount;
    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private AgoraStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "end_vote_count", nullable = false)
    private Integer endVoteCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "agora")
    private final List<AgoraMember> agoraMembers = new ArrayList<>();
    @OneToOne
    private AgoraThumbnail agoraThumbnail;

    @Builder
    public Agora(String title, Integer capacity, Integer duration, String color, Integer prosCount, Integer consCount,
                 Category category, AgoraThumbnail agoraThumbnail) {
        this.title = title;
        this.capacity = capacity;
        this.duration = duration;
        this.viewCount = 0;
        this.status = AgoraStatus.QUEUED;
        this.color = color;
        this.prosCount = prosCount;
        this.consCount = consCount;
        this.endVoteCount = 0;
        this.category = category;
        this.agoraThumbnail = agoraThumbnail;
    }

    public void addMember(AgoraMember member) {
        this.agoraMembers.add(member);
    }

    public void startAgora() {
        AgoraStatus expectedStatus = AgoraStatus.QUEUED;
        if (this.status == expectedStatus) {
            this.startTime = LocalDateTime.now();
            changeStatus(AgoraStatus.RUNNING);
        } else {
            throw new InvalidAgoraStatusException(expectedStatus);
        }
    }

    public void endVoteAgora(int participantsNum) {
        AgoraStatus expectedStatus = AgoraStatus.RUNNING;
        if (this.status == expectedStatus) {
            endVoteCount++;
            if (endVoteCount >= (int) (participantsNum * END_VOTE_RATIO)) {
                changeStatus(AgoraStatus.CLOSED);
                this.endTime = LocalDateTime.now();
            }
        } else {
            throw new InvalidAgoraStatusException(expectedStatus);
        }
    }

    public void endAgora() {
        changeStatus(AgoraStatus.CLOSED);
        this.endTime = LocalDateTime.now();
    }

    /**
     * 아고라 상태는 변경 순서가 있다.
     * QUEUED -> CLOSED로 변경될 수 없으므로 순차적으로 변경가능한지 확인 후 아고라 상태를 변경한다.
     */    public void changeStatus(AgoraStatus status) {
        if ((this.status == AgoraStatus.QUEUED && status == AgoraStatus.RUNNING) ||
                (this.status == AgoraStatus.RUNNING && status == AgoraStatus.CLOSED) ||
                (this.status == AgoraStatus.QUEUED && status == AgoraStatus.CLOSED)
        ) {
            this.status = status;
        } else {
            throw new InvalidAgoraStatusChangeException();
        }
    }

    public void updateProsCountAndConsCount(Integer prosCount, Integer consCount) {
        this.prosCount = prosCount;
        this.consCount = consCount;
    }

    public boolean isClosed() {
        return this.status == AgoraStatus.CLOSED;
    }

    public void checkAgoraVoteRequest(AgoraVoteRequest agoraVoteRequest) {
        if (agoraVoteRequest.voteType() == null) {
            throw new InvalidAgoraVoteTypeException();
        }
    }

    public void checkAgoraStatus() {
        if (this.status.equals(AgoraStatus.QUEUED) || this.status.equals(AgoraStatus.RUNNING)) {
            throw new InvalidAgoraStatusException(AgoraStatus.CLOSED);
        }
    }

    public void checkVoteTime() {
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(now, this.endTime).getSeconds() > 20) {
            throw new VoteTimeOutException();
        }
    }

    public boolean isAgoraClosed(Agora agora) {
        if (agora.status == AgoraStatus.CLOSED) {
            return true;
        }
        return false;
    }

    public void updateThumbnail(AgoraThumbnail agoraThumbnail) {
        this.agoraThumbnail = agoraThumbnail;
    }

    public boolean isTypeCapacityExceeded(int typeCount) {
        return typeCount >= this.capacity / 2;
    }
}
