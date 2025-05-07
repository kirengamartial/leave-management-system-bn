package com.martial.auth_service.service.impl;

import com.martial.auth_service.dto.AuthResponse;
import com.martial.auth_service.dto.LoginRequest;
import com.martial.auth_service.dto.RegisterRequest;
import com.martial.auth_service.model.Role;
import com.martial.auth_service.model.User;
import com.martial.auth_service.repository.UserRepository;
import com.martial.auth_service.security.JwtService;
import com.martial.auth_service.service.AuthService;
import com.martial.auth_service.service.EmailService;
import com.martial.auth_service.exception.EmailAlreadyRegisteredException;
import com.martial.auth_service.exception.UserNotFoundException;
import com.martial.auth_service.model.AuditLogType;
import com.martial.auth_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuditService auditService;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        auditService.logEvent(
                user.getId(),
                "EMAIL_VERIFICATION",
                "User email verified",
                AuditLogType.ACCOUNT_UPDATE);
    }

    @Override
    @Transactional
    public String handleOAuth2Login(String email, String firstName, String lastName, String googleId,
            String profilePicture) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Set<Role> userRoles = new HashSet<>();
                    userRoles.add(Role.STAFF);

                    User newUser = User.builder()
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .googleId(googleId)
                            .profilePicture(profilePicture)
                            .department("Not Set")
                            .isVerified(true)
                            .roles(userRoles)
                            .build();
                    return userRepository.save(newUser);
                });

        // Update profile picture on each login
        if (user.getGoogleId() != null) {
            user.setProfilePicture(profilePicture);
            userRepository.save(user);
        }

        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(request.getDepartment())
                .roles(Collections.singleton(Role.STAFF))
                .isVerified(false)
                .build();

        user = userRepository.save(user);

        // Generate verification token with minimal claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", "UNVERIFIED");
        String verificationToken = jwtService.generateToken(claims, user);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken, request.getRedirectUrl());

        // Log the registration event
        auditService.logEvent(
                user.getId(),
                "USER_REGISTRATION",
                "New user registered",
                AuditLogType.ACCOUNT_CREATION);

        // Generate login token for immediate use
        String loginToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(loginToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .department(user.getDepartment())
                .build();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}