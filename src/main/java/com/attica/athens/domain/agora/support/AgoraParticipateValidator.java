package com.attica.athens.domain.agora.support;

import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

public class AgoraParticipateValidator implements
        ConstraintValidator<ValidAgoraParticipateRequest, AgoraParticipateRequest> {

    @Override
    public boolean isValid(AgoraParticipateRequest request, ConstraintValidatorContext context) {
        boolean isTypeValid = Arrays.stream(AgoraMemberType.values())
                .anyMatch(type -> type.equals(request.type()));

        if (!isTypeValid) {
            context.buildConstraintViolationWithTemplate("Invalid user type.")
                    .addPropertyNode("type")
                    .addConstraintViolation();

            return false;
        }

        if (Objects.equals(request.type(), AgoraMemberType.OBSERVER)) {
            return true;
        }

        boolean isNicknameValid = request.nickname() == null || request.nickname().isBlank();
        boolean isPhotoNumValid = request.photoNum() == null;

        if (isNicknameValid) {
            context
                    .buildConstraintViolationWithTemplate("nickname can not be null")
                    .addPropertyNode("nickname")
                    .addConstraintViolation();

            return false;
        }

        if (isPhotoNumValid) {
            context.buildConstraintViolationWithTemplate("photoNum can not be null.")
                    .addPropertyNode("photoNum")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
