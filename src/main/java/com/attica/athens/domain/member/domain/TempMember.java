package com.attica.athens.domain.member.domain;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempMember extends BaseMember {

    private TempMember(MemberRole role) {
        super(role);
    }

    public static TempMember createTempUser() {
        return new TempMember(MemberRole.ROLE_TEMP_USER);
    }
}
