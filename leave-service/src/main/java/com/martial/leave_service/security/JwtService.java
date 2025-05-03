package com.martial.leave_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.severe("Error extracting username from token: " + e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.severe("Error extracting claim from token: " + e.getMessage());
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        try {
            logger.info("Generating token for user: " + userDetails.getUsername());
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.severe("Error generating token: " + e.getMessage());
            throw e;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.severe("Error validating token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                logger.severe("Invalid token format. Expected 3 parts but found: " + parts.length);
                throw new MalformedJwtException("Invalid token format");
            }

            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            logger.severe("JWT signature validation failed: " + e.getMessage());
            debugTokenDetails(token);
            throw e;
        } catch (ExpiredJwtException e) {
            logger.warning("JWT token is expired: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.severe("JWT token is malformed: " + e.getMessage());
            debugTokenDetails(token);
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.severe("JWT token is unsupported: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("JWT Validation Error: " + e.getMessage());
            throw e;
        }
    }

    private void debugTokenDetails(String token) {
        try {
            String[] parts = token.split("\\.");
            logger.info("Token parts count: " + parts.length);
            if (parts.length >= 2) {
                String header = new String(Base64.getUrlDecoder().decode(parts[0]));
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                logger.info("JWT Header: " + header);
                logger.info("JWT Payload: " + payload);
            }
        } catch (Exception e) {
            logger.warning("Could not parse token parts: " + e.getMessage());
        }
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.severe("Error creating signing key: " + e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            logger.info("Token successfully validated");
            return true;
        } catch (ExpiredJwtException ex) {
            logger.severe("JWT has expired");
            throw ex;
        } catch (MalformedJwtException ex) {
            logger.severe("Invalid JWT token");
            throw ex;
        } catch (SignatureException ex) {
            logger.severe("JWT signature does not match");
            throw ex;
        } catch (Exception e) {
            logger.severe("Token validation failed: " + e.getMessage());
            throw e;
        }
    }
}