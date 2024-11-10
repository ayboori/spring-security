package com.sparta.springsecurity.auth.domain.repository;

import com.sparta.springsecurity.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Optional<String> findRefreshTokenByUsername(String username);
}
