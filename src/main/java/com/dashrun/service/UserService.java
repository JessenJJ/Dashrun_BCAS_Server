package com.dashrun.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dashrun.entity.User;
import com.dashrun.payload.LoginResponse;
import com.dashrun.payload.LoginUserRequest;
import com.dashrun.payload.RegisterUserRequest;
import com.dashrun.payload.UserResponse;
import com.dashrun.repository.UserRepository;
import com.dashrun.security.BCrypt;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private Validator validator;

    @Autowired
    private Validator validator;

    public UserResponse register(RegisterUserRequest request,String token) {
        Set<ConstraintViolation<RegisterUserRequest>> constraintViolations = validator.validate(request);

        if (constraintViolations.size() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request.");
        }   

        List<User> allUsers = userRepository.findAll();
        if(!allUsers.isEmpty()) {

            User admin = userRepository.findFirstByToken(token).orElseThrow(() -> new 
                ResponseStatusException(HttpStatus.FORBIDDEN,"Request not allowed"));
            if (!admin.getRole().equals("admin")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Request not allowed");
            }
        }

        

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already registered.");
        }

                // if(!token.equals('admin')) {
        //      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not unautorized.");
        // }



        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());
        user.setRole(request.getRole());

        userRepository.save(user);

        UserResponse response = new UserResponse(user.getUsername(), user.getName(),user.getRole());

        return response;
    }

    @Transactional
    public LoginResponse login(LoginUserRequest request){
        Set<ConstraintViolation<LoginUserRequest>> constraintViolations = validator.validate(request);

        if(constraintViolations.size() != 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request");
        }

        User user = userRepository.findById(request.getUsername()).orElseThrow(() -> new 
        ResponseStatusException(HttpStatus.FORBIDDEN,"Username or password invalid"));

        if(BCrypt.checkpw(request.getPassword(),user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(nextExpired());

            userRepository.save(user);

            return LoginResponse.builder().username(user.getUsername()).name(user.getName())
            .token(user.getToken()).role(user.getRole()).build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Username or password invalid");
        }
    }

    private Long nextExpired(){
        Instant now = Instant.now();
        Instant next = now.plusSeconds(2 * 60 * 60);
        return next.toEpochMilli();
    }

    @Transactional
    public void logout(String token){
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid request");
        }
        User user = userRepository.findFirstByToken(token).orElseThrow(() -> new 
        ResponseStatusException(HttpStatus.UNAUTHORIZED,"User hasn't login"));

        if(user.getTokenExpiredAt() < Instant.now().toEpochMilli()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User has been logged out by system");
        }
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }
}
