package com.attica.athens.domain.user.api;

import com.attica.athens.domain.user.application.TempUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/temp-user")
@RequiredArgsConstructor
public class TempUserController {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private final TempUserService tempUserService;

    @PostMapping
    public ResponseEntity<String> postTempUser() {

        String token = tempUserService.createTempUser();

        return ResponseEntity.ok().header(AUTHORIZATION, BEARER + token).build();
    }
}
