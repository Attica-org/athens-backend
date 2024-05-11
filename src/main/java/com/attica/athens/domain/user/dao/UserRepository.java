package com.attica.athens.domain.user.dao;


import com.attica.athens.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
