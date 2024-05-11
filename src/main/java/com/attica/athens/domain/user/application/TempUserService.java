package com.attica.athens.domain.user.application;

import com.attica.athens.domain.user.domain.UserRole;
import com.attica.athens.global.security.JWTUtil;
import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.dao.TempUserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private static final long EXPIRED_MS = 60 * 60 * 10L;

    private final TempUserRepository tempUserRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public String createTempUser() {

        String tempUserId = UUID.randomUUID().toString();
        TempUser tempUser = TempUser.from(tempUserId);

        tempUserRepository.save(tempUser);

        return jwtUtil.createJwt(tempUserId, UserRole.ROLE_TEMP_USER.name(), EXPIRED_MS);
    }
}
