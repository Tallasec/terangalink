package com.terangalink.backend.security;

import com.terangalink.backend.entity.StudyGroup;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.MeetingType;
import com.terangalink.backend.repository.StudyGroupRepository;
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
class StudyGroupSecurityServiceTest {

    @Mock
    private StudyGroupRepository studyGroupRepository;

    @InjectMocks
    private StudyGroupSecurityService studyGroupSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessStudyGroup_shouldDenyAnonymousUser() {

        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(studyGroupSecurityService.canAccessStudyGroup(1L)).isFalse();
    }

    @Test
    void canAccessStudyGroup_shouldDenyWhenNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThat(studyGroupSecurityService.canAccessStudyGroup(1L)).isFalse();
    }

    @Test
    void canAccessStudyGroup_shouldAllowAdminOnActiveGroup() {

        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        when(studyGroupRepository.findById(10L))
                .thenReturn(Optional.of(sampleStudyGroup(10L, false)));

        assertThat(studyGroupSecurityService.canAccessStudyGroup(10L)).isTrue();
    }

    @Test
    void canAccessStudyGroup_shouldDenyAdminOnDeletedGroup() {

        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        when(studyGroupRepository.findById(10L))
                .thenReturn(Optional.of(sampleStudyGroup(10L, true)));

        assertThat(studyGroupSecurityService.canAccessStudyGroup(10L)).isFalse();
    }

    @Test
    void canAccessStudyGroup_shouldAllowCreator() {

        User creator = UserTestFixtures.sampleUser(42L);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        when(studyGroupRepository.findById(10L))
                .thenReturn(Optional.of(sampleStudyGroup(10L, false, creator)));

        assertThat(studyGroupSecurityService.canAccessStudyGroup(10L)).isTrue();
    }

    @Test
    void canAccessStudyGroup_shouldDenyOtherUser() {

        User creator = UserTestFixtures.sampleUser(42L);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(99L);
        setAuthentication(principal);

        when(studyGroupRepository.findById(10L))
                .thenReturn(Optional.of(sampleStudyGroup(10L, false, creator)));

        assertThat(studyGroupSecurityService.canAccessStudyGroup(10L)).isFalse();
    }

    @Test
    void canAccessStudyGroup_shouldDenyWhenGroupDoesNotExist() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        when(studyGroupRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThat(studyGroupSecurityService.canAccessStudyGroup(99L)).isFalse();
    }

    @Test
    void canAccessStudyGroup_shouldDenyWhenIdIsNull() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        assertThat(studyGroupSecurityService.canAccessStudyGroup(null)).isFalse();
        verify(studyGroupRepository, never()).findById(null);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private StudyGroup sampleStudyGroup(
            Long id,
            boolean deleted
    ) {
        return sampleStudyGroup(id, deleted, UserTestFixtures.sampleUser(42L));
    }

    private StudyGroup sampleStudyGroup(
            Long id,
            boolean deleted,
            User creator
    ) {

        StudyGroup studyGroup = new StudyGroup();

        studyGroup.setId(id);
        studyGroup.setTitle("Révisions Java");
        studyGroup.setSubject("Mathématiques");
        studyGroup.setDescription("Séance de révision.");
        studyGroup.setCity("Dakar");
        studyGroup.setLocation("Bibliothèque");
        studyGroup.setMeetingType(MeetingType.ONLINE);
        studyGroup.setMeetingDate(LocalDateTime.of(2026, 8, 1, 10, 0));
        studyGroup.setMaxMembers(8);
        studyGroup.setAvailable(true);
        studyGroup.setDeleted(deleted);
        studyGroup.setCreator(creator);

        return studyGroup;
    }
}
