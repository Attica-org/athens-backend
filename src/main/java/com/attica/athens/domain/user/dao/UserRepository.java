package com.attica.athens.domain.user.dao;


import com.attica.athens.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);
}
