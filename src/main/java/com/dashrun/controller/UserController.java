package com.dashrun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.dashrun.payload.LoginResponse;
import com.dashrun.payload.LoginUserRequest;
import com.dashrun.payload.RegisterUserRequest;
import com.dashrun.payload.UserResponse;
import com.dashrun.payload.WebResponse;
import com.dashrun.service.UserService;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping(
        path =  "/api/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<UserResponse> register(@RequestBody RegisterUserRequest request,
    @RequestHeader("X-API-TOKEN")String token){
        UserResponse userResponse = userService.register(request,token);
        
        // ini manual
        // WebResponse<UserResponse> response = new WebResponse<>();
        // response.setData(userResponse);
        // response.setErrors(null);

        return WebResponse.<UserResponse>builder().data(userResponse).errors(null).build();
    } 

    // public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
    //     return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    // }

    @PostMapping(
        path =  "/api/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<LoginResponse> login(@RequestBody LoginUserRequest request){
        LoginResponse response = userService.login(request);

        return WebResponse.<LoginResponse>builder().data(response).errors(null).build();
    }

    @PostMapping(
        path =  "/api/logout"
    )
    public WebResponse<String> logout(@RequestHeader("X-API-TOKEN")String token){
        userService.logout(token);

        return WebResponse.<String>builder().data("Log out success").errors(null).build();
    }
    
}
