package com.attica.athens.global.security;

import com.attica.athens.domain.user.dao.TempUserRepository;
import com.attica.athens.domain.user.dao.UserRepository;
import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TempUserRepository tempUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        UUID uuid = UUID.fromString(username);
        TempUser tempUser = tempUserRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(tempUser);
    }
}
