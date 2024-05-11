package com.attica.athens.agora.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category {

    @Id
    @Enumerated(EnumType.STRING)
    private CategoryName code;

    @ManyToOne()
    @JoinColumn(name = "parent_code")
    private Category parentCode;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private String name;

    @Builder
    public Category(CategoryName categoryName, Category category, Integer level, String name) {
        this.code = categoryName;
        this.parentCode = category;
        this.level = level;
        this.name = name;
    }
}
