package com.attica.athens.domain.user.application;

import com.attica.athens.domain.user.dao.TempUserRepository;
import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.domain.UserRole;
import com.attica.athens.global.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private final TempUserRepository tempUserRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public String createTempUser() {

        TempUser tempUser = TempUser.createTempUser();

        tempUserRepository.save(tempUser);

        return jwtUtil.createJwt(tempUser.getUuid().toString(), UserRole.ROLE_TEMP_USER.name());
    }
}
