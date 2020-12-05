package com.watchlist.backend.security;

import com.watchlist.backend.entities.db.Session;
import com.watchlist.backend.entities.db.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

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
        JwtBuilder builder = Jwts.builder();

        builder.signWith(SignatureAlgorithm.HS256, secret)
                .setSubject(email)
                .setExpiration(expiration)
                .claim(USER_ID, userId)
                .claim(USER_EMAIL, email);
        return builder.compact();
    }

    public boolean isValid(String token) {
        Jws<Claims> claims = parseClaims(token);

        return claims.getBody()
                .getExpiration()
                .after(new Date());
    }

    public UserPrincipal getUserPrincipal(String token) {
        UserPrincipal principal = new UserPrincipal();
        principal.setId(getUserId(token));
        principal.setEmail(getUserEmail(token));

        return principal;
    }

    public long getUserId(String token) {
        return parseClaims(token)
                .getBody()
                .get(USER_ID, Long.class);
    }

    public String getUserEmail(String token) {
        return parseClaims(token)
                .getBody()
                .get(USER_EMAIL, String.class);
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
    }
}
