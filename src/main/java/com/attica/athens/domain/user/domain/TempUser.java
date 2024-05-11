package com.attica.athens.domain.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "temp_user")
@Getter
public class TempUser extends BaseUser {

    @Id
    private String id;

    public TempUser(String id) {
        super(UserRole.ROLE_TEMP_USER);
        this.id = id;
    }
}
