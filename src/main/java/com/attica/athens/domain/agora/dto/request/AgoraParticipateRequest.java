package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AgoraParticipateRequest(
    @NotBlank
    String nickname,
    @NotNull
    Integer photoNum,
    @Pattern(regexp = "^(PROS|CONS|OBSERVER)$", message = "허용되지 않는 role 입니다.")
    String type
) {

    public AgoraUserType getAgoraUserType() {
        return AgoraUserType.valueOf(type);
    }
}
