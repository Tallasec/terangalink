package com.terangalink.backend.service;

import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.InvalidUserPatchException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.UserMapper;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailNormalizer emailNormalizer;
    private final EmailVerificationService emailVerificationService;
    private final CloudinaryService cloudinaryService;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            EmailNormalizer emailNormalizer,
            EmailVerificationService emailVerificationService,
            CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailNormalizer = emailNormalizer;
        this.emailVerificationService = emailVerificationService;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request) {
        String normalizedEmail = emailNormalizer.normalize(request.getEmail());
        request.setEmail(normalizedEmail);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    "Un utilisateur existe déjà avec cet email.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(Objects.requireNonNull(pageable))
                .map(userMapper::toResponseDto);
    }

    public UserResponseDTO getUserById(Long id) {
        return userMapper.toResponseDto(findUserByIdOrThrow(id));
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
        User user = findUserByIdOrThrow(id);
        validatePatchPayload(request);

        String currentEmail = user.getEmail();
        String normalizedEmail = normalizeAndValidateIncomingEmail(request);
        boolean emailChanged = normalizedEmail != null && !normalizedEmail.equalsIgnoreCase(currentEmail);
        validateEmailUniquenessForUpdate(normalizedEmail, user.getId());

        userMapper.updateEntityFromDto(request, user);

        if (emailChanged) {
            user.setEmail(currentEmail);
            emailVerificationService.issueEmailChangeVerificationCode(user, normalizedEmail);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO uploadProfileImage(Long id, MultipartFile file) {
        User user = findUserByIdOrThrow(id);
        validateProfileImage(file);

        String previousPublicId = user.getProfileImagePublicId();
        CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadProfileImage(file);

        user.setProfileImagePublicId(uploadResult.publicId());
        user.setProfileImageUrl(uploadResult.imageUrl());

        User savedUser = userRepository.save(user);

        if (previousPublicId != null && !previousPublicId.isBlank()) {
            try {
                cloudinaryService.deleteImage(previousPublicId);
            } catch (IllegalStateException ignored) {
                // On conserve la nouvelle image même si la suppression Cloudinary échoue.
            }
        }

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserByIdOrThrow(id);
        userRepository.delete(Objects.requireNonNull(user));
    }

    public UserResponseDTO getUserByEmail(String email) {
        String normalizedEmail = emailNormalizer.normalize(email);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'email fourni."));
        return userMapper.toResponseDto(user);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmailIgnoreCase(emailNormalizer.normalize(email));
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + id));
    }

    private String normalizeAndValidateIncomingEmail(UpdateUserRequestDTO request) {
        if (request.getEmail() == null) {
            return null;
        }

        String normalizedEmail = emailNormalizer.normalize(request.getEmail());
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new InvalidUserPatchException("L'email ne peut pas etre vide.");
        }

        request.setEmail(normalizedEmail);
        return normalizedEmail;
    }

    private void validateEmailUniquenessForUpdate(String normalizedEmail, Long currentUserId) {
        if (normalizedEmail == null) {
            return;
        }

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, currentUserId)) {
            throw new EmailAlreadyExistsException("Un utilisateur existe déjà avec cet email.");
        }
    }

    private void validatePatchPayload(UpdateUserRequestDTO request) {
        if (request == null || isPatchEmpty(request)) {
            throw new InvalidUserPatchException(
                    "Le corps PATCH ne peut pas etre vide. Fournissez au moins un champ a modifier.");
        }
    }

    private void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier image ne peut pas etre vide.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("La taille de l'image ne doit pas depasser 5 MB.");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Format d'image invalide. Formats autorises : jpg, jpeg, png, webp.");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isPatchEmpty(UpdateUserRequestDTO request) {
        return request.getFirstName() == null
                && request.getLastName() == null
                && request.getEmail() == null
                && request.getPassword() == null
                && request.getUniversity() == null
                && request.getFieldOfStudy() == null
                && request.getCity() == null;
    }
}
