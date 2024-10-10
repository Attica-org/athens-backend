package com.attica.athens.domain.agora.domain;


import com.attica.athens.domain.common.AuditingFields;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "agora_thumbnail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgoraThumbnail extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originName;

    private String imageUrl;

    public AgoraThumbnail(String originName, String imageUrl) {
        this.originName = originName;
        this.imageUrl = imageUrl;
    }
}
