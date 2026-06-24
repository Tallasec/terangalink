package com.terangalink.backend.service;

import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.exception.business.EmailAlreadyExistsException;
import com.terangalink.backend.exception.business.InvalidUserPatchException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.UserMapper;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true) // Pour dire par défaut toutes les méthodes de cette classe sont dans une transaction en lecture seule.
// Sauf les méthodes qui ont ça @Transactional
// C'est pour la performance
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailNormalizer emailNormalizer;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            EmailNormalizer emailNormalizer) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailNormalizer = emailNormalizer;
    }

    @Transactional
    // pour creer un user
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

    // pour récupérer tous les users
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(Objects.requireNonNull(pageable)) //Vérifie que pageable n'est pas null sinon leve NullPointerException
                .map(userMapper::toResponseDto); // Pour chaque User, appelle toResponseDto(User)
    }

    // pour récupérer un user par son id
    public UserResponseDTO getUserById(Long id) {
        return userMapper.toResponseDto(findUserByIdOrThrow(id));
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
        User user = findUserByIdOrThrow(id);
        validatePatchPayload(request); // pour ne pas accepter un request null (tous les champs vides)
        normalizeAndValidateIncomingEmail(request); //pour exiger l'adresse email
        validateEmailUniquenessForUpdate(request.getEmail(), user.getId()); // pour éviter d'utiliser un email qui existee deja

        userMapper.updateEntityFromDto(request, user);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Le role n'est pas modifiable via les endpoints publics user.
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userMapper.toResponseDto(userRepository.save(user));
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

    private void normalizeAndValidateIncomingEmail(UpdateUserRequestDTO request) {
        String normalizedEmail = emailNormalizer.normalize(request.getEmail());
        if (request.getEmail() != null && (normalizedEmail == null || normalizedEmail.isBlank())) {
            throw new InvalidUserPatchException("L'email ne peut pas etre vide.");
        }
        request.setEmail(normalizedEmail);
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
