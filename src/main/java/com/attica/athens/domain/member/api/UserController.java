package com.attica.athens.domain.member.api;

import com.attica.athens.domain.member.application.UserService;
import com.attica.athens.domain.member.dto.request.CreateUserRequest;
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
