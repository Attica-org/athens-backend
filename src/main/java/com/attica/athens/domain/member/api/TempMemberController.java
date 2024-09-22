package com.attica.athens.domain.member.api;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.member.application.TempMemberService;
import com.attica.athens.global.auth.dto.response.CreateAccessTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/temp-user")
@RequiredArgsConstructor
public class TempMemberController {

    private final TempMemberService tempMemberService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> postTempMember(HttpServletResponse response) {

        String accessToken = tempMemberService.createTempUser(response);

        return ResponseEntity.ok(ApiUtil.success(new CreateAccessTokenResponse(accessToken)));
    }
}
