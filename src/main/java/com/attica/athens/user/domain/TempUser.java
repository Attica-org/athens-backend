package com.attica.athens.user.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@AttributeOverride(name = "id", column = @Column(name = "temp_user_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "temp_user")
public class TempUser extends BaseUser {

    public TempUser(String id) {
        super(id, UserRole.ROLE_TEMP_USER);
    }
}
