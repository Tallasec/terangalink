package com.terangalink.backend.security;

import com.terangalink.backend.entity.Association;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.AssociationType;
import com.terangalink.backend.repository.AssociationRepository;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssociationSecurityServiceTest {

    @Mock
    private AssociationRepository associationRepository;

    @InjectMocks
    private AssociationSecurityService associationSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessAssociation_shouldDenyAnonymousUser() {

        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(associationSecurityService.canAccessAssociation(1L)).isFalse();
    }

    @Test
    void canAccessAssociation_shouldDenyWhenNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThat(associationSecurityService.canAccessAssociation(1L)).isFalse();
    }

    @Test
    void canAccessAssociation_shouldAllowAdminOnActiveAssociation() {

        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        when(associationRepository.findById(10L))
                .thenReturn(Optional.of(sampleAssociation(10L, false)));

        assertThat(associationSecurityService.canAccessAssociation(10L)).isTrue();
    }

    @Test
    void canAccessAssociation_shouldDenyAdminOnDeletedAssociation() {

        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        when(associationRepository.findById(10L))
                .thenReturn(Optional.of(sampleAssociation(10L, true)));

        assertThat(associationSecurityService.canAccessAssociation(10L)).isFalse();
    }

    @Test
    void canAccessAssociation_shouldAllowCreator() {

        User creator = UserTestFixtures.sampleUser(42L);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        when(associationRepository.findById(10L))
                .thenReturn(Optional.of(sampleAssociation(10L, false, creator)));

        assertThat(associationSecurityService.canAccessAssociation(10L)).isTrue();
    }

    @Test
    void canAccessAssociation_shouldDenyOtherUser() {

        User creator = UserTestFixtures.sampleUser(42L);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(99L);
        setAuthentication(principal);

        when(associationRepository.findById(10L))
                .thenReturn(Optional.of(sampleAssociation(10L, false, creator)));

        assertThat(associationSecurityService.canAccessAssociation(10L)).isFalse();
    }

    @Test
    void canAccessAssociation_shouldDenyWhenAssociationDoesNotExist() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        when(associationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThat(associationSecurityService.canAccessAssociation(99L)).isFalse();
    }

    @Test
    void canAccessAssociation_shouldDenyWhenIdIsNull() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        assertThat(associationSecurityService.canAccessAssociation(null)).isFalse();
        verify(associationRepository, never()).findById(null);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private Association sampleAssociation(
            Long id,
            boolean deleted
    ) {
        return sampleAssociation(id, deleted, UserTestFixtures.sampleUser(42L));
    }

    private Association sampleAssociation(
            Long id,
            boolean deleted,
            User creator
    ) {

        Association association = new Association();

        association.setId(id);
        association.setTitle("TerangaLink Association");
        association.setDescription("Association etudiante et culturelle.");
        association.setCity("Dakar");
        association.setAddress("Plateau");
        association.setContactEmail("contact@terangalink.org");
        association.setPhone("+221770000000");
        association.setWebsite("https://terangalink.org");
        association.setLogoUrl("https://terangalink.org/logo.png");
        association.setAssociationType(AssociationType.DAHIRA);
        association.setAvailable(true);
        association.setDeleted(deleted);
        association.setCreator(creator);
        association.setCreatedAt(LocalDateTime.now());
        association.setUpdatedAt(LocalDateTime.now());

        return association;
    }
}
