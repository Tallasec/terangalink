package com.terangalink.backend.service;

import com.terangalink.backend.entity.JobApplication;
import com.terangalink.backend.entity.JobPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.JobApplicationStatus;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.InvalidJobApplicationException;
import com.terangalink.backend.exception.business.JobApplicationNotFoundException;
import com.terangalink.backend.exception.business.JobPostNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.JobApplicationMapper;
import com.terangalink.backend.repository.JobApplicationRepository;
import com.terangalink.backend.repository.JobPostRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateJobApplicationRequestDTO;
import com.terangalink.backend.responseDTO.JobApplicationResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class JobApplicationService {

    private static final long MAX_CV_SIZE_BYTES = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_CV_EXTENSIONS = Set.of("pdf", "doc", "docx");

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final JobApplicationMapper jobApplicationMapper;
    private final CloudinaryService cloudinaryService;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            JobPostRepository jobPostRepository,
            UserRepository userRepository,
            JobApplicationMapper jobApplicationMapper,
            CloudinaryService cloudinaryService
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobPostRepository = jobPostRepository;
        this.userRepository = userRepository;
        this.jobApplicationMapper = jobApplicationMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public JobApplicationResponseDTO applyToJob(
            Long jobPostId,
            CreateJobApplicationRequestDTO request,
            MultipartFile cvFile
    ) {
        if (request == null) {
            throw new InvalidJobApplicationException(
                    "Le numero de telephone et le CV sont obligatoires.");
        }

        UserPrincipal principal = getCurrentPrincipal();
        User applicant = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));
        JobPost jobPost = findJobPostByIdOrThrow(jobPostId);
        validateCvFile(cvFile);

        if (jobPost.getOwner() != null && jobPost.getOwner().getId().equals(applicant.getId())) {
            throw new InvalidJobApplicationException("Vous ne pouvez pas postuler a votre propre offre.");
        }

        if (!jobPost.isAvailable()) {
            throw new InvalidJobApplicationException("Cette offre n'est plus disponible.");
        }

        if (jobApplicationRepository
                .findByJobPostIdAndApplicantIdAndStatus(
                        jobPostId,
                        applicant.getId(),
                        JobApplicationStatus.APPLIED)
                .isPresent()) {
            throw new InvalidJobApplicationException("Vous avez deja postule a cette offre.");
        }

        JobApplication application = jobApplicationMapper.toEntity(request);
        application.setJobPost(jobPost);
        application.setApplicant(applicant);
        String sanitizedFilename = cvFile.getOriginalFilename() != null ? cvFile.getOriginalFilename().trim() : "cv";
        CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadJobApplicationCv(cvFile);
        application.setCvPublicId(uploadResult.publicId());
        application.setCvUrl(uploadResult.imageUrl());
        application.setCvOriginalFilename(sanitizedFilename.isBlank() ? "cv" : sanitizedFilename);
        application.setStatus(JobApplicationStatus.APPLIED);

        return jobApplicationMapper.toResponseDto(jobApplicationRepository.save(application));
    }

    public JobApplicationResponseDTO getMyApplicationForJob(Long jobPostId) {
        UserPrincipal principal = getCurrentPrincipal();

        return jobApplicationRepository
                .findByJobPostIdAndApplicantIdAndStatus(
                        jobPostId,
                        principal.getId(),
                        JobApplicationStatus.APPLIED)
                .map(jobApplicationMapper::toResponseDto)
                .orElse(null);
    }

    public List<JobApplicationResponseDTO> getMyApplications() {
        UserPrincipal principal = getCurrentPrincipal();

        return jobApplicationRepository.findByApplicantIdOrderByCreatedAtDesc(principal.getId()).stream()
                .map(jobApplicationMapper::toResponseDto)
                .toList();
    }

    public List<JobApplicationResponseDTO> getJobApplications(Long jobPostId) {
        findJobPostByIdOrThrow(jobPostId);

        return jobApplicationRepository.findByJobPostIdOrderByCreatedAtDesc(jobPostId).stream()
                .map(jobApplicationMapper::toResponseDto)
                .toList();
    }

    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new InvalidCredentialsException("Utilisateur non authentifie.");
        }

        return principal;
    }

    private JobPost findJobPostByIdOrThrow(Long id) {
        return jobPostRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new JobPostNotFoundException(
                        "Offre d'emploi introuvable avec l'id : " + id));
    }

    private JobApplication findJobApplicationByIdOrThrow(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new JobApplicationNotFoundException(
                        "Candidature introuvable avec l'id : " + id));
    }

    private boolean canManageApplication(JobApplication application, UserPrincipal principal) {
        if (principal.getRole() == Role.ADMIN) {
            return true;
        }

        if (application.getApplicant() != null && application.getApplicant().getId().equals(principal.getId())) {
            return true;
        }

        return application.getJobPost() != null
                && application.getJobPost().getOwner() != null
                && application.getJobPost().getOwner().getId().equals(principal.getId());
    }

    private void validateCvFile(MultipartFile cvFile) {
        if (cvFile == null || cvFile.isEmpty()) {
            throw new InvalidJobApplicationException("Le CV est obligatoire.");
        }

        if (cvFile.getSize() > MAX_CV_SIZE_BYTES) {
            throw new InvalidJobApplicationException("Le CV ne doit pas depasser 10 MB.");
        }

        String extension = extractExtension(cvFile.getOriginalFilename());
        if (!ALLOWED_CV_EXTENSIONS.contains(extension)) {
            throw new InvalidJobApplicationException("Formats de CV autorises : pdf, doc, docx.");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
