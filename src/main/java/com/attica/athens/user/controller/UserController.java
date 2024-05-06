package com.attica.athens.user.controller;

import com.attica.athens.user.dto.CreateUserRequest;
import com.attica.athens.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public String postUser(CreateUserRequest createUserRequest) {

        userService.createUser(createUserRequest);

        return "ok";
    }
}
