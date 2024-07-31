package com.attica.athens.domain.member.application;

import com.attica.athens.domain.member.dao.TempUserRepository;
import com.attica.athens.domain.member.domain.TempMember;
import com.attica.athens.global.auth.application.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private final TempUserRepository tempUserRepository;
    private final AuthService authService;

    @Transactional
    public String createTempUser(HttpServletResponse response) {

        TempMember tempUser = TempMember.createTempUser();

        tempUserRepository.save(tempUser);

        return authService.createRefreshTokenAndGetAccessToken(tempUser.getId(), tempUser.getRole().name(), response);
    }
}
