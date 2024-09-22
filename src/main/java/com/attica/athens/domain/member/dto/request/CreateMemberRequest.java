package com.attica.athens.domain.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMemberRequest {

    private String username;
    private String password;

}
