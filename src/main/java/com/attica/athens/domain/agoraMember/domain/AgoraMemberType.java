package com.attica.athens.domain.agoraMember.domain;

public enum AgoraMemberType {
    PROS,
    CONS,
    OBSERVER;

    public boolean canSendMessage() {
        return this == PROS || this == CONS;
    }
}
