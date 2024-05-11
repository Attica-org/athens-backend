package com.attica.athens.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempUser extends BaseUser {

    @Id
    @Column(name = "temp_user_id")
    private String id;

    private TempUser(String id) {
        super(UserRole.ROLE_TEMP_USER);
        this.id = id;
    }

    public static TempUser from(String id) {
        return new TempUser(id);
    }
}
