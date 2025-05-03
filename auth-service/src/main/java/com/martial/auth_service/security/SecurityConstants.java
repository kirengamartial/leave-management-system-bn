package com.martial.auth_service.security;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 86400000; // 24 hours in milliseconds
    public static final String JWT_SECRET = System.getenv("JWT_SECRET");
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}