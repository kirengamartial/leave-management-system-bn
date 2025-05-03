package com.martial.auth_service.security;

import com.martial.auth_service.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private AuthService authService;

    // Using setter injection instead of constructor injection to break the circular
    // dependency
    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String googleId = oAuth2User.getAttribute("sub");
        String profilePicture = oAuth2User.getAttribute("picture");

        String redirectUrl = request.getParameter("redirect_uri");
        String token = authService.handleOAuth2Login(email, firstName, lastName, googleId, profilePicture);

        // Fetch user from DB to get all info
        com.martial.auth_service.model.User user = authService.getUserByEmail(email);
        StringBuilder targetUrl = new StringBuilder();
        if (redirectUrl != null) {
            targetUrl.append(redirectUrl).append("?token=").append(token);
        } else {
            targetUrl.append("/oauth2/redirect?token=").append(token);
        }
        targetUrl.append("&userId=").append(user.getId());
        targetUrl.append("&email=").append(user.getEmail());
        targetUrl.append("&firstName=").append(user.getFirstName());
        targetUrl.append("&lastName=").append(user.getLastName());
        targetUrl.append("&department=").append(user.getDepartment());
        targetUrl.append("&profilePicture=").append(user.getProfilePicture());
        targetUrl.append("&roles=").append(user.getRoles().toString());

        getRedirectStrategy().sendRedirect(request, response, targetUrl.toString());
    }
}