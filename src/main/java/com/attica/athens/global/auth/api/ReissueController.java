package com.attica.athens.global.auth.api;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ReissueController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<?>> reissue(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = authService.reissueRefreshToken(request, response);

        return ResponseEntity.ok(ApiUtil.success(accessToken));
    }
}
