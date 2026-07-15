package com.terangalink.backend.service;

import com.terangalink.backend.entity.JobPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.ContractType;
import com.terangalink.backend.exception.business.JobPostNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.JobPostMapper;
import com.terangalink.backend.repository.JobPostRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateJobPostRequestDTO;
import com.terangalink.backend.requestDTO.UpdateJobPostRequestDTO;
import com.terangalink.backend.responseDTO.JobPostResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobPostServiceTest {

    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobPostMapper jobPostMapper;

    @InjectMocks
    private JobPostService jobPostService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createJobPost_shouldCreateOffer() {

        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        User owner = UserTestFixtures.sampleUser(1L);
        CreateJobPostRequestDTO request = createRequest();
        JobPost jobPost = sampleJobPost(null, owner);
        JobPost savedJobPost = sampleJobPost(10L, owner);
        JobPostResponseDTO response = sampleResponse(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));
        when(jobPostMapper.toEntity(request))
                .thenReturn(jobPost);
        when(jobPostRepository.save(jobPost))
                .thenReturn(savedJobPost);
        when(jobPostMapper.toResponseDto(savedJobPost))
                .thenReturn(response);

        JobPostResponseDTO result =
                jobPostService.createJobPost(request);

        assertThat(result).isEqualTo(response);
        assertThat(jobPost.getOwner()).isEqualTo(owner);
        verify(jobPostRepository).save(jobPost);
    }

    @Test
    void createJobPost_shouldThrowWhenCurrentUserDoesNotExist() {

        CreateJobPostRequestDTO request = createRequest();
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        setAuthentication(principal);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jobPostService.createJobPost(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Utilisateur introuvable avec l'id : 1");

        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void getJobPostById_shouldReturnOffer() {

        User owner = UserTestFixtures.sampleUser(1L);
        JobPost jobPost = sampleJobPost(10L, owner);
        JobPostResponseDTO response = sampleResponse(10L);

        when(jobPostRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(jobPost));
        when(jobPostMapper.toResponseDto(jobPost))
                .thenReturn(response);

        assertThat(jobPostService.getJobPostById(10L)).isEqualTo(response);
    }

    @Test
    void getJobPostById_shouldThrowWhenOfferNotFound() {

        when(jobPostRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jobPostService.getJobPostById(99L))
                .isInstanceOf(JobPostNotFoundException.class)
                .hasMessage("Offre d'emploi introuvable avec l'id : 99");
    }

    @Test
    void getAllJobPosts_shouldReturnMappedPage() {

        PageRequest pageable = PageRequest.of(0, 20);
        User owner = UserTestFixtures.sampleUser(1L);
        JobPost jobPost = sampleJobPost(10L, owner);
        JobPostResponseDTO response = sampleResponse(10L);
        Page<JobPost> page = new PageImpl<>(List.of(jobPost), pageable, 1);

        when(jobPostRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(jobPostMapper.toResponseDto(jobPost))
                .thenReturn(response);

        Page<JobPostResponseDTO> result = jobPostService.getAllJobPosts(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchJobPosts_shouldReturnMappedPage() {

        PageRequest pageable = PageRequest.of(0, 20);
        User owner = UserTestFixtures.sampleUser(1L);
        JobPost jobPost = sampleJobPost(10L, owner);
        JobPostResponseDTO response = sampleResponse(10L);
        Page<JobPost> page = new PageImpl<>(List.of(jobPost), pageable, 1);

        when(jobPostRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(jobPostMapper.toResponseDto(jobPost))
                .thenReturn(response);

        Page<JobPostResponseDTO> result = jobPostService.searchJobPosts(
                "Developpeur",
                "Paris",
                "TerangaLink",
                ContractType.CDI,
                new BigDecimal("500"),
                new BigDecimal("1000"),
                true,
                pageable
        );

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void updateJobPost_shouldPatchOffer() {

        User owner = UserTestFixtures.sampleUser(1L);
        JobPost jobPost = sampleJobPost(10L, owner);
        UpdateJobPostRequestDTO request = updateRequest();
        JobPostResponseDTO response = sampleResponse(10L);

        when(jobPostRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(jobPost))
                .thenReturn(jobPost);
        when(jobPostMapper.toResponseDto(jobPost))
                .thenReturn(response);

        JobPostResponseDTO result = jobPostService.updateJobPost(10L, request);

        assertThat(result).isEqualTo(response);
        verify(jobPostMapper).updateEntity(jobPost, request);
    }

    @Test
    void deleteJobPost_shouldSoftDeleteOffer() {

        User owner = UserTestFixtures.sampleUser(1L);
        JobPost jobPost = sampleJobPost(10L, owner);

        when(jobPostRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(jobPost))
                .thenReturn(jobPost);

        jobPostService.deleteJobPost(10L);

        assertThat(jobPost.isDeleted()).isTrue();
        verify(jobPostRepository).save(jobPost);
    }

    private void setAuthentication(UserPrincipal principal) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()));
    }

    private CreateJobPostRequestDTO createRequest() {

        CreateJobPostRequestDTO dto =
                new CreateJobPostRequestDTO();

        dto.setTitle("Developpeur Java Spring Boot");
        dto.setDescription("Poste backend chez TerangaLink.");
        dto.setCompanyName("TerangaLink");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContractType(ContractType.CDI);
        dto.setSalary(new BigDecimal("1200.00"));
        dto.setAvailable(true);

        return dto;
    }

    private UpdateJobPostRequestDTO updateRequest() {

        UpdateJobPostRequestDTO dto =
                new UpdateJobPostRequestDTO();

        dto.setTitle("Developpeur Senior Java");
        dto.setSalary(new BigDecimal("1500.00"));

        return dto;
    }

    private JobPost sampleJobPost(
            Long id,
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
        jobPost.setDeleted(false);
        jobPost.setOwner(owner);
        jobPost.setCreatedAt(LocalDateTime.now());
        jobPost.setUpdatedAt(LocalDateTime.now());

        return jobPost;
    }

    private JobPostResponseDTO sampleResponse(Long id) {

        JobPostResponseDTO dto =
                new JobPostResponseDTO();

        dto.setId(id);
        dto.setTitle("Developpeur Java Spring Boot");
        dto.setDescription("Poste backend chez TerangaLink.");
        dto.setCompanyName("TerangaLink");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContractType(ContractType.CDI);
        dto.setSalary(new BigDecimal("1200.00"));
        dto.setAvailable(true);
        dto.setOwnerId(1L);
        dto.setOwnerFirstName("Alice");
        dto.setOwnerLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        return dto;
    }
}
