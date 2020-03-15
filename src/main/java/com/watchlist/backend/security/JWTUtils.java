package com.watchlist.backend.security;

import com.watchlist.backend.model.Session;
import com.watchlist.backend.model.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {
    static final String USER_ID = "userId";
    static final String USER_EMAIL = "userEmail";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.lifetime}")
    private long lifetime;

    public Session makeSessionFor(User user) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + lifetime);
        String token = makeToken(user.getId(), user.getEmail(), expiration);

        Session session = new Session();
        session.setExpiration(expiration);
        session.setToken(token);
        session.setUser(user);
        return session;
    }

    private String makeToken(long userId, String email, Date expiration) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(USER_ID, userId);
        claims.put(USER_EMAIL, email);

        JwtBuilder builder = Jwts.builder();

        builder.setSubject(email)
                .setExpiration(expiration)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret);
        return builder.compact();
    }
}
