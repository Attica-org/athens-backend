package com.attica.athens.domain.member.api;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.member.application.MemberService;
import com.attica.athens.domain.member.dto.request.CreateMemberRequest;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.domain.AuthProvider;
import com.attica.athens.global.auth.dto.response.CreateAccessTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/open/member")
public class MemberOpenController {

    private final MemberService memberService;
    private final AuthService authService;

    /**
     * 회원가입 API
     *
     * @param response
     * @param createMemberRequest
     * @return ApiResponse<?>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> postUser(HttpServletResponse response,
                                                   CreateMemberRequest createMemberRequest) {

        return ResponseEntity.ok(ApiUtil.success(new CreateAccessTokenResponse(
                memberService.createMember(createMemberRequest, AuthProvider.LOCAL, response))));
    }

    /**
     * 토큰을 받아오는 API
     *
     * @param tempToken
     * @return Map<String, String> : access_token
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam("temp-token") String tempToken) {

        return ResponseEntity.ok(Map.of("accessToken", authService.getAccessToken(tempToken)));
    }
}
