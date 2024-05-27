package com.attica.athens.domain.user.api;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.user.application.TempUserService;
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
public class TempUserController {

    private final TempUserService tempUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> postTempUser(HttpServletResponse response) {

        String accessToken = tempUserService.createTempUser(response);

        return ResponseEntity.ok(ApiUtil.success(new CreateAccessTokenResponse(accessToken)));
    }
}
