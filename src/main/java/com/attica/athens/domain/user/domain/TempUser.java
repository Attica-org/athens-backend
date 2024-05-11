package com.attica.athens.domain.user.domain;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempUser extends BaseUser {

    private TempUser(UserRole role) {
        super(role);
    }

    public static TempUser createTempUser() {
        return new TempUser(UserRole.ROLE_TEMP_USER);
    }
}
