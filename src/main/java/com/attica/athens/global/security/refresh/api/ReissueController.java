package com.attica.athens.global.security.refresh.api;

import com.attica.athens.global.security.refresh.application.ReissueService;
import com.attica.athens.global.security.refresh.dto.CreateCookieResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        CreateCookieResponse createCookieResponse = reissueService.reissueRefreshToken(request, response);

        response.addCookie(createCookieResponse.cookie()[0]);
        response.addCookie(createCookieResponse.cookie()[1]);

        return ResponseEntity.ok().build();
    }
}
