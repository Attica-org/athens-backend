package com.attica.athens.global.auth;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member user = memberRepository.findByUsername(username)
                .orElse(null);
        if (user != null) {
            return new CustomUserDetails(user.getId(), user.getPassword(), user.getRole().name());
        }

        throw new UsernameNotFoundException("User not found");
    }
}
