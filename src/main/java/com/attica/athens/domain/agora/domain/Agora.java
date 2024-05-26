package com.attica.athens.domain.agora.domain;

import com.attica.athens.domain.agora.exception.InvalidAgoraStatusChangeException;
import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private AgoraStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "start_time", updatable = false)
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "agora")
    private final List<AgoraUser> agoraUsers = new ArrayList<>();

    public Agora(String title, Integer capacity, Integer duration, String color, Category category) {
        this.title = title;
        this.capacity = capacity;
        this.duration = duration;
        this.viewCount = 0;
        this.status = AgoraStatus.QUEUED;
        this.color = color;
        this.category = category;
    }

    public void changeStatus(AgoraStatus status) {
        if ((this.status == AgoraStatus.QUEUED && status == AgoraStatus.RUNNING) ||
                (this.status == AgoraStatus.RUNNING && status == AgoraStatus.CLOSED)
        ) {
            this.status = status;
        } else {
            throw new InvalidAgoraStatusChangeException(id);
        }
    }

    public void startAgora() {
        if (this.status == AgoraStatus.QUEUED) {
            this.startTime = LocalDateTime.now();
            changeStatus(AgoraStatus.RUNNING);
        } else {
            throw new InvalidAgoraStatusException(AgoraStatus.QUEUED);
        }
    }
}
