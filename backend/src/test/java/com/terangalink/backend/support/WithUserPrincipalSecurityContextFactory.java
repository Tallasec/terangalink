package com.terangalink.backend.support;

import com.terangalink.backend.entity.User;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithUserPrincipalSecurityContextFactory
        implements WithSecurityContextFactory<WithUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithUserPrincipal annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = UserTestFixtures.sampleUser(annotation.id());
        user.setRole(annotation.role());
        UserPrincipal principal = UserPrincipal.from(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}
