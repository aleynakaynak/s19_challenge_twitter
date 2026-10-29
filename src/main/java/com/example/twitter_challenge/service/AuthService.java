package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.LoginRequest;
import com.example.twitter_challenge.dto.RegisterRequest;
import com.example.twitter_challenge.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    UserResponse login(LoginRequest request);
}
