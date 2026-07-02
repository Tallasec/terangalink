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
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HousingImageSecurityServiceTest {

    @Mock
    private HousingRepository housingRepository;

    @InjectMocks
    private HousingImageSecurityService housingImageSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canManageHousingImages_shouldDenyAnonymousUser() {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(housingImageSecurityService.canManageHousingImages(1L)).isFalse();
    }

    @Test
    void canManageHousingImages_shouldAllowAdmin() {
        setAuthentication(AuthTestFixtures.adminUserPrincipal(1L));

        assertThat(housingImageSecurityService.canManageHousingImages(99L)).isTrue();
    }

    @Test
    void canManageHousingImages_shouldAllowOwner() {
        User owner = UserTestFixtures.sampleUser(42L);
        setAuthentication(AuthTestFixtures.sampleUserPrincipal(42L));
        when(housingRepository.findById(10L)).thenReturn(Optional.of(sampleHousing(10L, owner)));

        assertThat(housingImageSecurityService.canManageHousingImages(10L)).isTrue();
    }

    @Test
    void canManageHousingImages_shouldDenyOtherUser() {
        User owner = UserTestFixtures.sampleUser(42L);
        setAuthentication(AuthTestFixtures.sampleUserPrincipal(43L));
        when(housingRepository.findById(10L)).thenReturn(Optional.of(sampleHousing(10L, owner)));

        assertThat(housingImageSecurityService.canManageHousingImages(10L)).isFalse();
    }

    @Test
    void canManageHousingImages_shouldDenyWhenHousingDoesNotExist() {
        setAuthentication(AuthTestFixtures.sampleUserPrincipal(42L));
        when(housingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(housingImageSecurityService.canManageHousingImages(99L)).isFalse();
    }

    private void setAuthentication(UserPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private HousingPost sampleHousing(Long id, User owner) {
        HousingPost housing = new HousingPost();
        housing.setId(id);
        housing.setTitle("Studio meuble proche campus");
        housing.setCity("Paris");
        housing.setPrice(new BigDecimal("750.00"));
        housing.setHousingType(HousingType.STUDIO);
        housing.setAvailable(true);
        housing.setOwner(owner);
        return housing;
    }
}
