package com.attica.athens.domain.member.dto.response;

import com.attica.athens.domain.member.domain.Member;

public record GetMemberResponse(String authProvider, String nickname, String email) {

    public static GetMemberResponse from(Member member) {;
        return new GetMemberResponse(member.getAuthProvider().name(), member.getNickname(),
                member.getEmail());
    }
}
