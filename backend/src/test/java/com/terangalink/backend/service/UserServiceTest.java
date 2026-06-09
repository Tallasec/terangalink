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
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du {@link UserService} : logique métier isolée des dépendances
 * (repository, mapper, encodeur de mot de passe, normalisation d'email).
 * <p>
 * Utile pour valider rapidement les règles métier et les exceptions sans base
 * de données ni contexte Spring complet.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailNormalizer emailNormalizer;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldNormalizeEmailEncodePasswordAndReturnDto() {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();
        User entity = UserTestFixtures.sampleUser(null);
        User savedUser = UserTestFixtures.sampleUser(1L);
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.existsByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(UserTestFixtures.VALID_PASSWORD)).thenReturn("encoded-password");
        when(userRepository.save(entity)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(response);

        UserResponseDTO result = userService.createUser(request);

        assertThat(result).isEqualTo(response);
        assertThat(request.getEmail()).isEqualTo(UserTestFixtures.NORMALIZED_EMAIL);
        assertThat(entity.getPassword()).isEqualTo("encoded-password");
        assertThat(entity.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void createUser_shouldThrowWhenEmailAlreadyExists() {
        CreateUserRequestDTO request = UserTestFixtures.validCreateRequest();

        when(emailNormalizer.normalize(request.getEmail())).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.existsByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Un utilisateur existe déjà avec cet email.");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_shouldReturnMappedDto() {
        User user = UserTestFixtures.sampleUser(1L);
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(response);

        assertThat(userService.getUserById(1L)).isEqualTo(response);
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Utilisateur introuvable avec l'id : 99");
    }

    @Test
    void getUserByEmail_shouldNormalizeEmailAndReturnDto() {
        User user = UserTestFixtures.sampleUser(1L);
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);

        when(emailNormalizer.normalize("Alice@Example.com")).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL))
                .thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(response);

        assertThat(userService.getUserByEmail("Alice@Example.com")).isEqualTo(response);
    }

    @Test
    void getUserByEmail_shouldThrowWhenUserNotFound() {
        when(emailNormalizer.normalize(UserTestFixtures.VALID_EMAIL)).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.findByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(UserTestFixtures.VALID_EMAIL))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Utilisateur introuvable avec l'email fourni.");
    }

    @Test
    void getAllUsers_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 20);
        User user = UserTestFixtures.sampleUser(1L);
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponseDto(user)).thenReturn(response);

        Page<UserResponseDTO> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateUser_shouldApplyPatchAndReturnDto() {
        UpdateUserRequestDTO request = UserTestFixtures.validUpdateRequest();
        User existingUser = UserTestFixtures.sampleUser(1L);
        User savedUser = UserTestFixtures.sampleUser(1L);
        UserResponseDTO response = UserTestFixtures.sampleUserResponse(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(response);

        assertThat(userService.updateUser(1L, request)).isEqualTo(response);
        verify(userMapper).updateEntityFromDto(request, existingUser);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUser_shouldEncodePasswordWhenProvided() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setPassword(UserTestFixtures.VALID_PASSWORD);
        User existingUser = UserTestFixtures.sampleUser(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(UserTestFixtures.VALID_PASSWORD)).thenReturn("new-encoded-password");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toResponseDto(existingUser)).thenReturn(UserTestFixtures.sampleUserResponse(1L));

        userService.updateUser(1L, request);

        assertThat(existingUser.getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, UserTestFixtures.validUpdateRequest()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUser_shouldThrowWhenPatchIsEmpty() {
        User existingUser = UserTestFixtures.sampleUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, new UpdateUserRequestDTO()))
                .isInstanceOf(InvalidUserPatchException.class)
                .hasMessageContaining("Le corps PATCH ne peut pas etre vide");
    }

    @Test
    void updateUser_shouldThrowWhenEmailAlreadyUsedByAnotherUser() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setEmail("other@example.com");
        User existingUser = UserTestFixtures.sampleUser(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(emailNormalizer.normalize("other@example.com")).thenReturn("other@example.com");
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("other@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Un utilisateur existe déjà avec cet email.");
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        User user = UserTestFixtures.sampleUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any());
    }

    @Test
    void emailExists_shouldDelegateToRepositoryWithNormalizedEmail() {
        when(emailNormalizer.normalize(UserTestFixtures.VALID_EMAIL)).thenReturn(UserTestFixtures.NORMALIZED_EMAIL);
        when(userRepository.existsByEmailIgnoreCase(UserTestFixtures.NORMALIZED_EMAIL)).thenReturn(true);

        assertThat(userService.emailExists(UserTestFixtures.VALID_EMAIL)).isTrue();

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).existsByEmailIgnoreCase(emailCaptor.capture());
        assertThat(emailCaptor.getValue()).isEqualTo(UserTestFixtures.NORMALIZED_EMAIL);
    }
}
