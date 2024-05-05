package com.attica.athens.user.service;

import com.attica.athens.user.domain.User;
import com.attica.athens.user.dto.CreateUserRequest;
import com.attica.athens.user.repository.UserRepository;
import java.util.UUID;
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

        String userId = UUID.randomUUID().toString();
        User user = new User(userId, username, bCryptPasswordEncoder.encode(password));

        userRepository.save(user);
    }
}
