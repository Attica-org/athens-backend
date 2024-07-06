package com.attica.athens.domain.agoraUser.domain;

public enum AgoraUserType {
    PROS,
    CONS,
    OBSERVER;

    public boolean canSendMessage() {
        return this == PROS || this == CONS;
    }
}
