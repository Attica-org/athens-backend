package com.attica.athens.domain.user.dao;


import com.attica.athens.domain.user.domain.TempUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempUserRepository extends JpaRepository<TempUser, Integer> {

}