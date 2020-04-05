package com.watchlist.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ResourceAccessChecker {

    /**
     * Checks if principal from given <code>auth</code> can
     * access <code>userId</code> resources
     *
     * @param auth Currently logged user auth
     * @param userId User which resources are trying to be accessed
     * @return true if access is granted
     */
    public boolean userLists(Authentication auth, long userId) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getId() == userId;
    }
}
