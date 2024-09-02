package com.attica.athens.global.auth.application;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.Member;
import com.attica.athens.global.auth.domain.CustomUserDetails;
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

        Member member = memberRepository.findByUsername(username)
                .orElse(null);
        if (member != null) {
            return new CustomUserDetails(member.getId(), member.getPassword(), member.getRole().name());
        }

        throw new UsernameNotFoundException("User not found");
    }
}
