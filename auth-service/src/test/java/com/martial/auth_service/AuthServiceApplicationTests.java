package com.martial.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import com.martial.auth_service.service.EmailService;
import com.martial.auth_service.service.AuditService;
import com.martial.auth_service.repository.UserRepository;
import com.martial.auth_service.security.JwtService;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceApplicationTests {

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private EmailService emailService;

	@MockBean
	private AuditService auditService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private JwtService jwtService;

	@Test
	void contextLoads() {
	}
}
