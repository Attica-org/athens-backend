package com.attica.athens.domain.member.api;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.member.application.MemberService;
import com.attica.athens.domain.member.dto.request.CreateMemberRequest;
import com.attica.athens.domain.member.exception.InvalidTempTokenException;
import com.attica.athens.global.auth.domain.AuthProvider;
import com.attica.athens.global.auth.dto.response.CreateAccessTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/open/member")
public class MemberOpenController {

    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;

    public MemberOpenController(final MemberService memberService,
                                @Qualifier("redisTemplate") final RedisTemplate<String, String> redisTemplate) {
        this.memberService = memberService;
        this.redisTemplate = redisTemplate;
    }

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
        String accessToken = redisTemplate.opsForValue()
                .get(tempToken);
        if (accessToken != null) {
            redisTemplate.delete(tempToken);
            return ResponseEntity.ok(Map.of("accessToken", accessToken));
        }
        throw new InvalidTempTokenException();
    }
}
