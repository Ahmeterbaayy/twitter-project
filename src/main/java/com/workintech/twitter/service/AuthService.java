package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.LoginRequest;
import com.workintech.twitter.dto.request.RegisterRequest;
import com.workintech.twitter.dto.response.AuthResponse;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
}
