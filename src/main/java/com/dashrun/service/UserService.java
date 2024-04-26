package com.dashrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dashrun.entity.User;
import com.dashrun.payload.RegisterUserRequest;
import com.dashrun.payload.UserResponse;
import com.dashrun.repository.UserRepository;
import com.dashrun.security.BCrypt;

import jakarta.validation.Validator;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private Validator validator;

    public UserResponse register(RegisterUserRequest request) {

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already registered.");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));

        userRepository.save(user);

        UserResponse response = new UserResponse(user.getUsername(), user.getName());

        return response;
    }

}
