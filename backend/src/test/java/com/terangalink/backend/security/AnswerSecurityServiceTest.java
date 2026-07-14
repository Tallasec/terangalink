package com.terangalink.backend.security;

import com.terangalink.backend.entity.Answer;
import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.repository.AnswerRepository;
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
class AnswerSecurityServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerSecurityService answerSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessAnswer_shouldDenyAnonymousUser() {

        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(
                answerSecurityService.canAccessAnswer(1L)
        ).isFalse();
    }

    @Test
    void canAccessAnswer_shouldDenyWhenNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThat(
                answerSecurityService.canAccessAnswer(1L)
        ).isFalse();
    }

    @Test
    void canAccessAnswer_shouldAllowAdmin() {

        UserPrincipal admin =
                AuthTestFixtures.adminUserPrincipal(1L);

        setAuthentication(admin);

        assertThat(
                answerSecurityService.canAccessAnswer(10L)
        ).isTrue();

        verify(answerRepository, never()).findById(10L);
    }

    @Test
    void canAccessAnswer_shouldAllowAuthor() {

        User author = UserTestFixtures.sampleUser(42L);
        Answer answer = sampleAnswer(10L, author);
        UserPrincipal principal =
                AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);

        when(answerRepository.findById(10L))
                .thenReturn(Optional.of(answer));

        assertThat(
                answerSecurityService.canAccessAnswer(10L)
        ).isTrue();
    }

    @Test
    void canAccessAnswer_shouldDenyOtherUser() {

        User author = UserTestFixtures.sampleUser(42L);
        Answer answer = sampleAnswer(10L, author);
        UserPrincipal otherUser =
                AuthTestFixtures.sampleUserPrincipal(99L);

        setAuthentication(otherUser);

        when(answerRepository.findById(10L))
                .thenReturn(Optional.of(answer));

        assertThat(
                answerSecurityService.canAccessAnswer(10L)
        ).isFalse();
    }

    @Test
    void canAccessAnswer_shouldDenyWhenAnswerDoesNotExist() {

        UserPrincipal principal =
                AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);

        when(answerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThat(
                answerSecurityService.canAccessAnswer(99L)
        ).isFalse();
    }

    @Test
    void canAccessAnswer_shouldDenyDeletedAnswer() {

        User author = UserTestFixtures.sampleUser(42L);
        Answer answer = sampleAnswer(10L, author);
        answer.setDeleted(true);

        UserPrincipal principal =
                AuthTestFixtures.sampleUserPrincipal(42L);

        setAuthentication(principal);

        when(answerRepository.findById(10L))
                .thenReturn(Optional.of(answer));

        assertThat(
                answerSecurityService.canAccessAnswer(10L)
        ).isFalse();
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private Answer sampleAnswer(
            Long id,
            User author
    ) {

        ForumTopic topic = new ForumTopic();
        topic.setId(1L);
        topic.setTitle("Comment trouver un logement ?");
        topic.setContent("Je cherche un studio.");
        topic.setCategory(ForumCategory.LOGEMENT);
        topic.setAuthor(author);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());

        Answer answer = new Answer();
        answer.setId(id);
        answer.setContent("Merci pour cette précision.");
        answer.setForumTopic(topic);
        answer.setAuthor(author);
        answer.setDeleted(false);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());

        return answer;
    }
}
