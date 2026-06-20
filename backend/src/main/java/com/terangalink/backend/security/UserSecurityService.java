package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("userSecurityService")
public class UserSecurityService {

    public boolean canAccessUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            return false;
        }

        if (userPrincipal.getRole() == Role.ADMIN) {
            return true;
        }

        return userId != null && userId.equals(userPrincipal.getId());
    }
}
