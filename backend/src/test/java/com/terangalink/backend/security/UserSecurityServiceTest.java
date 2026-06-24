package com.terangalink.backend.security;

import com.terangalink.backend.enums.Role;
import com.terangalink.backend.support.AuthTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class UserSecurityServiceTest {

    private final UserSecurityService userSecurityService = new UserSecurityService();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessUser_shouldAllowAdminToAccessAnyUser() {
        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        assertThat(userSecurityService.canAccessUser(1L)).isTrue();
        assertThat(userSecurityService.canAccessUser(99L)).isTrue();
    }

    @Test
    void canAccessUser_shouldAllowUserToAccessOwnProfile() {
        UserPrincipal user = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(user);

        assertThat(userSecurityService.canAccessUser(42L)).isTrue();
    }

    @Test
    void canAccessUser_shouldDenyUserAccessToOtherProfile() {
        UserPrincipal user = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(user);

        assertThat(userSecurityService.canAccessUser(43L)).isFalse();
    }

    @Test
    void canAccessUser_shouldDenyWhenNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThat(userSecurityService.canAccessUser(1L)).isFalse();
    }

    @Test
    void canAccessUser_shouldDenyAnonymousUser() {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(userSecurityService.canAccessUser(1L)).isFalse();
    }

    @Test
    void canAccessUser_shouldDenyWhenPrincipalIsNotUserPrincipal() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "plain-string-principal", null, AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(userSecurityService.canAccessUser(1L)).isFalse();
    }

    @Test
    void canAccessUser_shouldDenyWhenUserIdIsNull() {
        UserPrincipal user = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(user);

        assertThat(userSecurityService.canAccessUser(null)).isFalse();
    }

    private void setAuthentication(UserPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }
}
