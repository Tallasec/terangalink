package com.terangalink.backend.mapper;

import com.terangalink.backend.requestDTO.CreateUserRequestDTO;
import com.terangalink.backend.requestDTO.UpdateUserRequestDTO;
import com.terangalink.backend.responseDTO.UserResponseDTO;
import com.terangalink.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequestDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUniversity(dto.getUniversity());
        user.setFieldOfStudy(dto.getFieldOfStudy());
        user.setCity(dto.getCity());
        return user;
    }

    public void updateEntityFromDto(UpdateUserRequestDTO dto, User user) {
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getUniversity() != null) {
            user.setUniversity(dto.getUniversity());
        }
        if (dto.getFieldOfStudy() != null) {
            user.setFieldOfStudy(dto.getFieldOfStudy());
        }
        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }
    }

    public UserResponseDTO toResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUniversity(user.getUniversity());
        dto.setFieldOfStudy(user.getFieldOfStudy());
        dto.setCity(user.getCity());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
