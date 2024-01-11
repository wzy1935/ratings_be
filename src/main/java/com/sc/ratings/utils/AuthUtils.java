package com.sc.ratings.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.crypto.SecretKey;

@Component
public class AuthUtils {
    @Value("${ratings.db-salt}")
    private String dbSalt;
    @Value("${ratings.jwt-secret}")
    private String jwtSecret;
    @Autowired
    private HttpServletRequest request;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String encode(String password) {
        return DigestUtils.md5DigestAsHex((dbSalt + password).getBytes());
    }

    public String createJwt(String userName) {
        return Jwts.builder().subject(userName).signWith(getSigningKey()).compact();
    }

    public String verifyJwt(String jwt) {
        if (jwt == null) return null;
        try {
            String userName = Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(jwt).getPayload().getSubject();
            return userName;
        } catch (JwtException e) {
            // if not valid
            return null;
        }
    }

    public String getCurrentUserName() {
        String jwt = request.getHeader("Authorization");
        return verifyJwt(jwt);
    }
}
