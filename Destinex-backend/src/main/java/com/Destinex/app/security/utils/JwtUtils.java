package com.Destinex.app.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtUtils {

   // private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final String SECRET = "your-very-long-random-secret-string"; // from properties ideally
    private static final SecretKey  key = Keys.hmacShaKeyFor(SECRET.getBytes()); // fixed key
    private static final long EXPIRATION_MS = 3600000; // 1 hour
    private Claims claims;
    public static String generateToken(String email, int id,  List<String> roles) {
        return Jwts.builder()
                // the payload is email + userId + roles as array of strings
                .claim("email", email)
                .claim("id", id)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    // validate token — throws exceptions if invalid
    public  void validateToken(String token) {
        claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token) // throws unchecked exceptions if invalid
                .getPayload();

    }

    public  Integer getId(String token) {
        return this.claims.get("id", Integer.class);
    }

    public  String getEmail(String token) {
        return this.claims.get("email", String.class);
    }

    public  List<String> getRoles(String token) {
/*
        List<String> roles = ((List<?>) claims.get("roles")).stream()
                .map(Object::toString)
                .toList();
        */
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) this.claims.get("roles");

        return roles;
    }
}
