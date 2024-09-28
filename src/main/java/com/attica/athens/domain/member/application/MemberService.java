package com.attica.athens.domain.member.application;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.Member;
import com.attica.athens.domain.member.dto.request.CreateMemberRequest;
import com.attica.athens.domain.member.dto.response.GetMemberResponse;
import com.attica.athens.domain.member.exception.DuplicateMemberException;
import com.attica.athens.domain.member.exception.NotFoundMemberException;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.domain.AuthProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;

    /**
     * 로컬 회원 가입
     *
     * @param createMemberRequest
     * @param authProvider
     */
    @Transactional
    public String createMember(CreateMemberRequest createMemberRequest, AuthProvider authProvider,
                               HttpServletResponse response) {

        String username = createMemberRequest.getUsername();
        String password = createMemberRequest.getPassword();

        Boolean isExist = memberRepository.existsByUsername(username);
        if (isExist) {
            throw new DuplicateMemberException();
        }

        Member member = Member.createMember(username, bCryptPasswordEncoder.encode(password), authProvider, null);

        memberRepository.save(member);

        return authService.createRefreshTokenAndGetAccessToken(member.getId(), member.getRole().name(),
                response);
    }

    public GetMemberResponse getMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundMemberException(userId));
        return GetMemberResponse.from(member);
    }
}
