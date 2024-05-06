package com.attica.athens.user.repository;


import com.attica.athens.user.domain.TempUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempUserRepository extends JpaRepository<TempUser, Integer> {

}
