package com.example.demo.service;

import com.example.demo.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {
    private final String SECRET_KEY = "f85cbf079eacded5ebe7fa095e9b4a58b137ecc84a999bac921b4822e6d5b0f1";
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }
    public boolean isValid(String token, UserDetails user) {
        try {
            String username = extractUsername(token);
            boolean valid = username.equals(user.getUsername()) && !isTokenExpired(token);
            return valid;
        } catch (Exception e) {
            logger.error("Token validation error: ", e);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        boolean expired = expirationDate.before(new Date());
        logger.debug("Token expiration date: " + expirationDate + ", expired: " + expired);
        return expired;
    }


    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token parsing failed", e);
        }
    }

    public String generateToken(User user){
        long expirationTime = 1000 * 60 * 60 * 10;
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
        return token;
    }
    public String refreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return generateTokenWithClaims(claims);
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return generateTokenWithClaims(claims);
        }
    }

    private String generateTokenWithClaims(Claims claims) {
        long expirationTime = 1000 * 60 * 60 * 10;
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }
    private SecretKey getSigningKey() {
        byte [] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}