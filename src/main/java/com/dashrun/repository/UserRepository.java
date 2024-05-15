package com.dashrun.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dashrun.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findFirstByToken(String token);
    Optional<User> findFirstByRole(String token);
    Optional<User> findFirstByUsername(String token);

    boolean existsByUsername(String username);
}
