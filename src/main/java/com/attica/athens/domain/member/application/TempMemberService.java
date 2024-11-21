package com.attica.athens.domain.member.application;

import com.attica.athens.domain.member.dao.TempMemberRepository;
import com.attica.athens.domain.member.domain.TempMember;
import com.attica.athens.global.auth.application.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempMemberService {

    private final TempMemberRepository tempMemberRepository;
    private final AuthService authService;

    @Transactional
    public String createTempUser(HttpServletResponse response) {

        TempMember tempUser = TempMember.createTempUser();

        tempMemberRepository.save(tempUser);

        Long userId = tempUser.getId();
        String userRole = tempUser.getRole().name();
        authService.createRefreshToken(userId, userRole, response);
        return authService.createAccessToken(userId, userRole);
    }
}
