package com.attica.athens.domain.member.api;

import com.attica.athens.domain.member.application.MemberService;
import com.attica.athens.domain.member.dto.request.CreateMemberRequest;
import com.attica.athens.global.auth.domain.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public String postUser(CreateMemberRequest createMemberRequest) {

        memberService.createMember(createMemberRequest, ProviderType.LOCAL);

        return "ok";
    }
}
