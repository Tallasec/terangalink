package com.terangalink.backend.security;

import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.repository.ForumTopicRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
FORUM TOPIC SECURITY SERVICE TEST

Teste les autorisations d'accès
aux sujets du forum.
*/

@ExtendWith(MockitoExtension.class)
class ForumTopicSecurityServiceTest {

    @Mock
    private ForumTopicRepository forumTopicRepository;

    @InjectMocks
    private ForumTopicSecurityService forumTopicSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessForumTopic_shouldDenyAnonymousUser() {

        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(1L)
        ).isFalse();
    }

    @Test
    void canAccessForumTopic_shouldDenyWhenNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(1L)
        ).isFalse();
    }

    @Test
    void canAccessForumTopic_shouldAllowAdmin() {

        UserPrincipal admin =
                AuthTestFixtures.adminUserPrincipal(1L);

        setAuthentication(admin);

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(10L)
        ).isTrue();

        verify(forumTopicRepository, never()).findById(10L);
    }

    @Test
    void canAccessForumTopic_shouldAllowAuthor() {

        User author = UserTestFixtures.sampleUser(42L);

        ForumTopic topic =
                sampleForumTopic(10L, author);

        UserPrincipal principal =
                AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);

        when(forumTopicRepository.findById(10L))
                .thenReturn(Optional.of(topic));

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(10L)
        ).isTrue();
    }

    @Test
    void canAccessForumTopic_shouldDenyOtherUser() {

        User author = UserTestFixtures.sampleUser(42L);

        ForumTopic topic =
                sampleForumTopic(10L, author);

        UserPrincipal otherUser =
                AuthTestFixtures.sampleUserPrincipal(99L);

        setAuthentication(otherUser);

        when(forumTopicRepository.findById(10L))
                .thenReturn(Optional.of(topic));

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(10L)
        ).isFalse();
    }

    @Test
    void canAccessForumTopic_shouldDenyWhenTopicDoesNotExist() {

        UserPrincipal principal =
                AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);

        when(forumTopicRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(99L)
        ).isFalse();
    }

    @Test
    void canAccessForumTopic_shouldDenyDeletedTopic() {

        User author = UserTestFixtures.sampleUser(42L);

        ForumTopic topic =
                sampleForumTopic(10L, author);

        topic.setDeleted(true);

        UserPrincipal principal =
                AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);

        when(forumTopicRepository.findById(10L))
                .thenReturn(Optional.of(topic));

        assertThat(
                forumTopicSecurityService.canAccessForumTopic(10L)
        ).isFalse();
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private ForumTopic sampleForumTopic(
            Long id,
            User author
    ) {

        ForumTopic topic = new ForumTopic();

        topic.setId(id);
        topic.setTitle("Comment trouver un logement ?");
        topic.setContent("Je cherche un studio.");
        topic.setCategory(ForumCategory.LOGEMENT);
        topic.setAuthor(author);
        topic.setViews(0L);
        topic.setDeleted(false);

        return topic;
    }
}