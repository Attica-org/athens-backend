package com.attica.athens.domain.agora.support;

import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

public class AgoraParticipateValidator implements ConstraintValidator<ValidAgoraParticipateRequest, AgoraParticipateRequest> {

    @Override
    public boolean isValid(AgoraParticipateRequest request, ConstraintValidatorContext context) {
        boolean isTypeValid = Arrays.stream(AgoraUserType.values())
                .anyMatch(type -> type.name().equals(request.type()));

        if (!isTypeValid) {
            context.buildConstraintViolationWithTemplate("Invalid user type.")
                    .addPropertyNode("type")
                    .addConstraintViolation();
            return false;
        }

        if (Objects.equals(request.getAgoraUserType(), AgoraUserType.OBSERVER)) {
            return true;
        }

        boolean isNicknameValid = request.nickname() != null && !request.nickname().isEmpty() && !request.nickname().isBlank();
        boolean isPhotoNumValid = request.photoNum() != null;

        if (!isNicknameValid || !isPhotoNumValid) {

            if (!isNicknameValid) {
                context
                        .buildConstraintViolationWithTemplate("nickname can not be null")
                        .addPropertyNode("nickname")
                        .addConstraintViolation();

            }

            if (!isPhotoNumValid) {
                context.buildConstraintViolationWithTemplate("photoNum can not be null.")
                        .addPropertyNode("photoNum")
                        .addConstraintViolation();
            }

            return false;
        }

        return true;
    }
}
