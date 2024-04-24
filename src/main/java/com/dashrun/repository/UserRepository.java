package com.dashrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dashrun.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    
}
