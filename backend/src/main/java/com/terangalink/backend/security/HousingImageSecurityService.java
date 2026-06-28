package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import com.terangalink.backend.repository.HousingRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("housingImageSecurityService")
public class HousingImageSecurityService {

    private final HousingRepository housingRepository;

    public HousingImageSecurityService(HousingRepository housingRepository) {
        this.housingRepository = housingRepository;
    }

    public boolean canManageHousingImages(Long housingId) {
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

        if (housingId == null) {
            return false;
        }

        return housingRepository.findById(housingId)
                .map(housingPost -> housingPost.getOwner() != null
                        && housingPost.getOwner().getId() != null
                        && housingPost.getOwner().getId().equals(userPrincipal.getId()))
                .orElse(false);
    }
}
