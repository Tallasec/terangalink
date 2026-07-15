package com.terangalink.backend.security;

import com.terangalink.backend.entity.JobPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ContractType;
import com.terangalink.backend.repository.JobPostRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobPostSecurityServiceTest {

    @Mock
    private JobPostRepository jobPostRepository;

    @InjectMocks
    private JobPostSecurityService jobPostSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessJobPost_shouldDenyAnonymousUser() {

        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThat(jobPostSecurityService.canAccessJobPost(1L)).isFalse();
    }

    @Test
    void canAccessJobPost_shouldDenyWhenNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThat(jobPostSecurityService.canAccessJobPost(1L)).isFalse();
    }

    @Test
    void canAccessJobPost_shouldAllowAdminOnActiveOffer() {

        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        when(jobPostRepository.findById(10L))
                .thenReturn(Optional.of(sampleJobPost(10L, false)));

        assertThat(jobPostSecurityService.canAccessJobPost(10L)).isTrue();
    }

    @Test
    void canAccessJobPost_shouldDenyAdminOnDeletedOffer() {

        UserPrincipal admin = AuthTestFixtures.adminUserPrincipal(1L);
        setAuthentication(admin);

        when(jobPostRepository.findById(10L))
                .thenReturn(Optional.of(sampleJobPost(10L, true)));

        assertThat(jobPostSecurityService.canAccessJobPost(10L)).isFalse();
    }

    @Test
    void canAccessJobPost_shouldAllowOwner() {

        User owner = UserTestFixtures.sampleUser(42L);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        when(jobPostRepository.findById(10L))
                .thenReturn(Optional.of(sampleJobPost(10L, false, owner)));

        assertThat(jobPostSecurityService.canAccessJobPost(10L)).isTrue();
    }

    @Test
    void canAccessJobPost_shouldDenyOtherUser() {

        User owner = UserTestFixtures.sampleUser(42L);
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(99L);
        setAuthentication(principal);

        when(jobPostRepository.findById(10L))
                .thenReturn(Optional.of(sampleJobPost(10L, false, owner)));

        assertThat(jobPostSecurityService.canAccessJobPost(10L)).isFalse();
    }

    @Test
    void canAccessJobPost_shouldDenyWhenOfferDoesNotExist() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        when(jobPostRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThat(jobPostSecurityService.canAccessJobPost(99L)).isFalse();
    }

    @Test
    void canAccessJobPost_shouldDenyWhenIdIsNull() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);
        setAuthentication(principal);

        assertThat(jobPostSecurityService.canAccessJobPost(null)).isFalse();
        verify(jobPostRepository, never()).findById(null);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private JobPost sampleJobPost(
            Long id,
            boolean deleted
    ) {
        return sampleJobPost(id, deleted, UserTestFixtures.sampleUser(42L));
    }

    private JobPost sampleJobPost(
            Long id,
            boolean deleted,
            User owner
    ) {

        JobPost jobPost = new JobPost();

        jobPost.setId(id);
        jobPost.setTitle("Developpeur Java Spring Boot");
        jobPost.setDescription("Poste backend chez TerangaLink.");
        jobPost.setCompanyName("TerangaLink");
        jobPost.setCity("Dakar");
        jobPost.setAddress("Plateau");
        jobPost.setContractType(ContractType.CDI);
        jobPost.setSalary(new BigDecimal("1200.00"));
        jobPost.setAvailable(true);
        jobPost.setDeleted(deleted);
        jobPost.setOwner(owner);

        return jobPost;
    }
}
