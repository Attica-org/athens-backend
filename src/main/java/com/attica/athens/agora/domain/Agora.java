package com.attica.athens.agora.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

    private Integer duration;

    private Integer viewCount;

    private Integer status;

    @ManyToOne
    @JoinColumn(name = "code")
    private Category code;
}
