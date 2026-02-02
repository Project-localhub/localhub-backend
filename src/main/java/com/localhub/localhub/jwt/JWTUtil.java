package com.localhub.localhub.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;

@Component
public class JWTUtil {

    private SecretKey secretKey;


    public JWTUtil(@Value("${jwt.secret}") String secret) {

        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException
                    ("JWT secret key must be at least 32 characters.");
        }


        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());

    }

    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().get("role", String.class);
    }

    public String getCategory(String token) {


        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }


    public Boolean isExpired(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    //일반적인 jwt 로직
    public String createJwt(String category,String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Authentication toAuthentication(String token) {

        String username = getUsername(token);
        String role = getRole(token);

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    public Boolean isValid(String token, Boolean isAccess) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String type = claims.get("type", String.class);
            if (type == null) {
                return false;
            }
            if (isAccess && !type.equals("access")) return false;
            if (!isAccess && !type.equals("refresh")) return false;

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

    }
    //refresh access 확인가능 로직
    public String createJWT(String username, String role, Boolean isAccess,String category) {

        long now = System.currentTimeMillis();
        long expiry = isAccess ? 3600000 : 86400000;
        String type = isAccess ? "access" : "refresh";

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("type", type)
                .claim("category",category)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiry))
                .signWith(secretKey)
                .compact();


    }
}
