package com.attica.athens.domain.agora.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "parent_code")
    private Category parentCode;

    @Column(nullable = false)
    private Integer level;

    @Column(length = 50, nullable = false)
    private String name;

    private Category(String code, Category parentCode, Integer level, String name) {
        this.code = code;
        this.parentCode = parentCode;
        this.level = level;
        this.name = name;
    }

    public static Category createCategory(String code, Category parentCode, Integer level, String name) {
        return new Category(code, parentCode, level, name);
    }
}
