package com.martial.auth_service.controller;

import com.martial.auth_service.dto.AuthResponse;
import com.martial.auth_service.dto.LoginRequest;
import com.martial.auth_service.dto.RegisterRequest;
import com.martial.auth_service.model.Role;
import com.martial.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/register")
        public ResponseEntity<Object> register(
                        @RequestBody @Valid RegisterRequest request) {
                AuthResponse response = authService.register(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(Map.of(
                                                "message",
                                                "Registration successful. Please check your email for verification and check spam too.",
                                                "userId", response.getUserId()));
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                        @RequestBody @Valid LoginRequest request) {
                return ResponseEntity.ok(authService.login(request));
        }

        @GetMapping("/validate")
        public ResponseEntity<Boolean> validateToken(
                        @RequestParam String token) {
                return ResponseEntity.ok(authService.validateToken(token));
        }

        @GetMapping("/verify")
        public ResponseEntity<String> verifyEmail(@RequestParam String token) {
                authService.verifyEmail(token);
                return ResponseEntity.ok("Email verified successfully");
        }

        @GetMapping("/oauth2/google")
        public ResponseEntity<Object> getGoogleAuthUrl() {
                String clientId = System.getenv("GOOGLE_CLIENT_ID");
                String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");

                String authUrl = "https://accounts.google.com/o/oauth2/auth" +
                                "?client_id=" + clientId +
                                "&redirect_uri=" + redirectUri +
                                "&response_type=code" +
                                "&scope=email%20profile" +
                                "&access_type=offline";

                return ResponseEntity.ok(Map.of("authUrl", authUrl));
        }

        @GetMapping("/oauth2/redirect")
        public void oauth2Redirect(@RequestParam("code") String code, HttpServletResponse response)
                        throws java.io.IOException {
                String clientId = System.getenv("GOOGLE_CLIENT_ID");
                String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
                String googleRedirectUri = System.getenv("GOOGLE_REDIRECT_URI");
                String tokenEndpoint = "https://oauth2.googleapis.com/token";
                String userInfoEndpoint = "https://www.googleapis.com/oauth2/v2/userinfo";
                String frontendUrl = System.getenv("FRONTEND_URL_LOGIN");

                try {
                        // Exchange code for access token
                        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
                        String body = "code="
                                        + java.net.URLEncoder.encode(code, java.nio.charset.StandardCharsets.UTF_8)
                                        + "&client_id="
                                        + java.net.URLEncoder.encode(clientId, java.nio.charset.StandardCharsets.UTF_8)
                                        + "&client_secret="
                                        + java.net.URLEncoder.encode(clientSecret,
                                                        java.nio.charset.StandardCharsets.UTF_8)
                                        + "&redirect_uri="
                                        + java.net.URLEncoder.encode(googleRedirectUri,
                                                        java.nio.charset.StandardCharsets.UTF_8)
                                        + "&grant_type=authorization_code";
                        java.net.http.HttpRequest tokenRequest = java.net.http.HttpRequest.newBuilder()
                                        .uri(java.net.URI.create(tokenEndpoint))
                                        .header("Content-Type", "application/x-www-form-urlencoded")
                                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
                                        .build();
                        java.net.http.HttpResponse<String> tokenResponse = httpClient.send(tokenRequest,
                                        java.net.http.HttpResponse.BodyHandlers.ofString());
                        // Log token response status
                        System.out.println("Token response status: " + tokenResponse.statusCode());
                        if (tokenResponse.statusCode() != 200) {
                                System.out.println("Token response error: " + tokenResponse.body());
                                response.sendRedirect(frontendUrl + "?error=Failed+to+get+access+token+from+Google");
                                return;
                        }
                        com.fasterxml.jackson.databind.JsonNode tokenJson = new com.fasterxml.jackson.databind.ObjectMapper()
                                        .readTree(tokenResponse.body());
                        String accessToken = tokenJson.get("access_token").asText();

                        // Get user info from Google
                        java.net.http.HttpRequest userInfoRequest = java.net.http.HttpRequest.newBuilder()
                                        .uri(java.net.URI.create(userInfoEndpoint))
                                        .header("Authorization", "Bearer " + accessToken)
                                        .build();
                        java.net.http.HttpResponse<String> userInfoResponse = httpClient.send(userInfoRequest,
                                        java.net.http.HttpResponse.BodyHandlers.ofString());

                        if (userInfoResponse.statusCode() != 200) {
                                response.sendRedirect(frontendUrl + "?error=Failed+to+get+user+info+from+Google");
                                return;
                        }
                        com.fasterxml.jackson.databind.JsonNode userInfoJson = new com.fasterxml.jackson.databind.ObjectMapper()
                                        .readTree(userInfoResponse.body());
                        String email = userInfoJson.get("email").asText();
                        String firstName = userInfoJson.has("given_name") ? userInfoJson.get("given_name").asText()
                                        : "";
                        String lastName = userInfoJson.has("family_name") ? userInfoJson.get("family_name").asText()
                                        : "";
                        String googleId = userInfoJson.has("id") ? userInfoJson.get("id").asText() : "";
                        String profilePicture = userInfoJson.has("picture") ? userInfoJson.get("picture").asText()
                                        : null;

                        String token = authService.handleOAuth2Login(email, firstName, lastName, googleId,
                                        profilePicture);
                        com.martial.auth_service.model.User user = authService.getUserByEmail(email);
                        StringBuilder targetUrl = new StringBuilder();
                        targetUrl.append(frontendUrl).append("?token=").append(token);
                        targetUrl.append("&userId=").append(user.getId());
                        targetUrl.append("&email=").append(user.getEmail());
                        targetUrl.append("&firstName=").append(user.getFirstName());
                        targetUrl.append("&lastName=").append(user.getLastName());
                        targetUrl.append("&department=").append(user.getDepartment());
                        targetUrl.append("&profilePicture=").append(user.getProfilePicture());
                        targetUrl.append("&roles=").append(
                                        user.getRoles().stream()
                                                        .map(Role::name)
                                                        .collect(Collectors.joining(",")));
                        response.sendRedirect(targetUrl.toString());
                } catch (Exception e) {
                        // Log the specific exception
                        System.out.println("OAuth error: " + e.getClass().getName() + ": " + e.getMessage());
                        e.printStackTrace();
                        response.sendRedirect(frontendUrl + "?error=Google+OAuth+code+exchange+failed&message=" +
                                        java.net.URLEncoder.encode(e.getMessage(),
                                                        java.nio.charset.StandardCharsets.UTF_8));
                }
        }
}