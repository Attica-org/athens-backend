package com.attica.athens.user.controller;

import com.attica.athens.user.service.TempUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/temp-user")
@RequiredArgsConstructor
public class TempUserController {

    private final TempUserService tempUserService;

    @PostMapping
    public ResponseEntity<String> postTempUser() {

        String token = tempUserService.createTempUser();

        return ResponseEntity.ok().header("Authorization", "Bearer " + token).build();
    }

    @GetMapping("/test")
    public String test() {

        return "Test Controller";
    }
}
