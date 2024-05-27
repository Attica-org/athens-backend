
package com.attica.athens.global.auth;

import com.attica.athens.domain.user.dao.UserRepository;
import com.attica.athens.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user != null) {
            return new CustomUserDetails(user.getId(), user.getPassword(), user.getRole().name());
        }

        throw new UsernameNotFoundException("User not found");
    }
}