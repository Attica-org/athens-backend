package com.attica.athens.domain.user.application;

import com.attica.athens.domain.user.dao.TempUserRepository;
import com.attica.athens.domain.user.domain.TempUser;
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

        TempUser tempUser = TempUser.createTempUser();

        tempUserRepository.save(tempUser);

        return authService.createRefreshTokenAndGetAccessToken(tempUser.getId(), tempUser.getRole().name(), response);
    }
}
