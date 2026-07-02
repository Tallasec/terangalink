package com.terangalink.backend.security;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.HousingType;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HousingSecurityServiceTest {

    @Mock
    private HousingRepository housingRepository;

    @InjectMocks
    private HousingSecurityService housingSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessHousing_shouldDenyAnonymousUser() {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(housingSecurityService.canAccessHousing(1L)).isFalse();
    }

    @Test
    void canAccessHousing_shouldDenyWhenNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThat(housingSecurityService.canAccessHousing(1L)).isFalse();
    }

    @Test
    void canAccessHousing_shouldAllowAdminToAccessAnyHousing() {
        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        assertThat(housingSecurityService.canAccessHousing(99L)).isTrue();
        verify(housingRepository, never()).findById(99L);
    }

    @Test
    void canAccessHousing_shouldAllowOwnerToAccessHousing() {
        User owner = UserTestFixtures.sampleUser(42L);
        HousingPost housingPost = sampleHousing(10L, owner);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);
        when(housingRepository.findById(10L)).thenReturn(Optional.of(housingPost));

        assertThat(housingSecurityService.canAccessHousing(10L)).isTrue();
    }

    @Test
    void canAccessHousing_shouldDenyOtherUser() {
        User owner = UserTestFixtures.sampleUser(42L);
        HousingPost housingPost = sampleHousing(10L, owner);
        UserPrincipal otherUser = AuthTestFixtures.sampleUserPrincipal(43L);

        setAuthentication(otherUser);
        when(housingRepository.findById(10L)).thenReturn(Optional.of(housingPost));

        assertThat(housingSecurityService.canAccessHousing(10L)).isFalse();
    }

    @Test
    void canAccessHousing_shouldDenyWhenHousingDoesNotExist() {
        UserPrincipal user = AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(user);
        when(housingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(housingSecurityService.canAccessHousing(99L)).isFalse();
    }

    private void setAuthentication(UserPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private HousingPost sampleHousing(Long id, User owner) {
        HousingPost housingPost = new HousingPost();
        housingPost.setId(id);
        housingPost.setTitle("Studio meuble proche campus");
        housingPost.setCity("Paris");
        housingPost.setPrice(new BigDecimal("750.00"));
        housingPost.setHousingType(HousingType.STUDIO);
        housingPost.setAvailable(true);
        housingPost.setOwner(owner);
        return housingPost;
    }
}
