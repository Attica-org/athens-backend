package com.attica.athens.domain.agora.vote.dto;

public record KickVoteInfo(String nickname) {

    public String getMessage() {
        return this.nickname + "님이 추방 되셨습니다.";
    }
}
