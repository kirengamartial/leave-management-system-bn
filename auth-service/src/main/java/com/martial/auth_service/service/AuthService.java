package com.martial.auth_service.service;

import com.martial.auth_service.dto.AuthResponse;
import com.martial.auth_service.dto.LoginRequest;
import com.martial.auth_service.dto.RegisterRequest;
import com.martial.auth_service.model.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    boolean validateToken(String token);

    void verifyEmail(String token);

    String handleOAuth2Login(String email, String firstName, String lastName, String googleId, String profilePicture);

    User getUserByEmail(String email);
}