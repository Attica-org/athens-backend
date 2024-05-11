package com.attica.athens.domain.agora.domain;

import com.attica.athens.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
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
import java.time.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agora extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Duration duration;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private AgoraStatus status;

    @JoinColumn(name = "code")
    @ManyToOne
    private Category code;

    private Agora(String title, Integer capacity, Duration duration, Category code) {
        this.title = title;
        this.capacity = capacity;
        this.duration = duration;
        this.viewCount = 0;
        this.status = AgoraStatus.RUNNING;
        this.code = code;
    }

    public static Agora of(String title, Integer capacity, Duration duration, Category code) {
        return new Agora(title, capacity, duration, code);
    }
}
