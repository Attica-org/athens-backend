package com.attica.athens.domain.agora.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agora")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Agora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private LocalTime duration;

    @Column(nullable = false)
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    private AgoraStatus agoraStatus;

    @ManyToOne
    @JoinColumn(name = "code")
    private Category code;

    @Builder
    public Agora(String title, Integer capacity, LocalTime duration, Category code) {
        this.title = title;
        this.capacity = capacity;
        this.duration = duration;
        this.viewCount = 0;
        this.agoraStatus = AgoraStatus.RUNNING;
        this.code = code;
    }
}
