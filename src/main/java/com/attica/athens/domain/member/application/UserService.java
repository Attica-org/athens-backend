package com.attica.athens.domain.member.application;

import com.attica.athens.domain.member.dao.UserRepository;
import com.attica.athens.domain.member.domain.Member;
import com.attica.athens.domain.member.dto.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void createUser(CreateUserRequest createUserRequest) {

        String username = createUserRequest.getUsername();
        String password = createUserRequest.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) {
            throw new RuntimeException("User already exists");
        }

        Member user = Member.createUser(username, bCryptPasswordEncoder.encode(password));

        userRepository.save(user);
    }
}
