package com.attica.athens.domain.member.application;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.Member;
import com.attica.athens.domain.member.dto.request.CreateMemberRequest;
import com.attica.athens.global.auth.domain.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void createMember(CreateMemberRequest createMemberRequest, ProviderType providerType) {

        String username = createMemberRequest.getUsername();
        String password = createMemberRequest.getPassword();

        Boolean isExist = memberRepository.existsByUsername(username);
        if (isExist) {
            throw new RuntimeException("User already exists");
        }

        Member user = Member.createMember(username, bCryptPasswordEncoder.encode(password), providerType);

        memberRepository.save(user);
    }
}
