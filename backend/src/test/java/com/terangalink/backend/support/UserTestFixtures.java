package com.terangalink.backend.support;

import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;

import java.time.LocalDateTime;

public final class UserTestFixtures {

    public static final String VALID_PASSWORD = "Password1!";
    public static final String VALID_EMAIL = "alice@example.com";
    public static final String NORMALIZED_EMAIL = "alice@example.com";

    private UserTestFixtures() {
    }

    public static CreateUserRequestDTO validCreateRequest() {
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setFirstName("Alice");
        dto.setLastName("Dupont");
        dto.setEmail(VALID_EMAIL);
        dto.setPassword(VALID_PASSWORD);
        dto.setUniversity("Sorbonne");
        dto.setFieldOfStudy("Informatique");
        dto.setCity("Paris");
        return dto;
    }

    public static UpdateUserRequestDTO validUpdateRequest() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setFirstName("Alicia");
        dto.setLastName("Martin");
        return dto;
    }

    public static User sampleUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Alice");
        user.setLastName("Dupont");
        user.setEmail(NORMALIZED_EMAIL);
        user.setPassword("encoded-password");
        user.setUniversity("Sorbonne");
        user.setFieldOfStudy("Informatique");
        user.setCity("Paris");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.of(2025, 1, 15, 10, 30));
        return user;
    }

    public static UserResponseDTO sampleUserResponse(Long id) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setFirstName("Alice");
        dto.setLastName("Dupont");
        dto.setEmail(NORMALIZED_EMAIL);
        dto.setUniversity("Sorbonne");
        dto.setFieldOfStudy("Informatique");
        dto.setCity("Paris");
        dto.setRole(Role.USER);
        dto.setCreatedAt(LocalDateTime.of(2025, 1, 15, 10, 30));
        return dto;
    }
}
