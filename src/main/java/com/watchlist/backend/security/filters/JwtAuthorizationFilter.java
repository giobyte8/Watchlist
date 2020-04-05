package com.watchlist.backend.security.filters;

import com.watchlist.backend.security.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JWTUtils jwtUtils;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JWTUtils jwtUtils) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            chain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (!StringUtils.isEmpty(authHeader)) {
            String jwt = StringUtils.delete(authHeader, "Bearer ");

            try {
                if (jwtUtils.isValid(jwt)) {

                    return new UsernamePasswordAuthenticationToken(
                            jwtUtils.getUserPrincipal(jwt),
                            jwt,
                            null
                    );
                }
            } catch (ExpiredJwtException exception) {
                log.warn(
                        "Request to parse expired JWT: {} failed: {}",
                        jwt,
                        exception.getMessage()
                );
            } catch (UnsupportedJwtException exception) {
                log.warn(
                        "Request to parse unsupported JWT: {} failed: {}",
                        jwt,
                        exception.getMessage()
                );
            } catch (MalformedJwtException exception) {
                log.warn(
                        "Request to parse invalid JWT: {} failed: {}",
                        jwt,
                        exception.getMessage()
                );
            } catch (SignatureException exception) {
                log.warn(
                        "Request to parse JWT with invalid signature: {} failed: {}",
                        jwt,
                        exception.getMessage()
                );
            } catch (IllegalArgumentException exception) {
                log.warn(
                        "Request to parse empty or null JWT: {} failed: {}",
                        jwt,
                        exception.getMessage()
                );
            }
        }

        return null;
    }
}
