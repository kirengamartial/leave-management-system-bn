package com.martial.leave_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.martial.leave_service.service.LeaveService;
import com.martial.leave_service.repository.UserRepository;
import com.martial.leave_service.security.CustomUserDetailsService;
import com.martial.leave_service.security.JwtService;

@SpringBootTest
class LeaveServiceApplicationTests {

	@MockBean
	private CustomUserDetailsService customUserDetailsService;

	@MockBean
	private LeaveService leaveService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private UserRepository userRepository;

	@Test
	void contextLoads() {
	}
}
