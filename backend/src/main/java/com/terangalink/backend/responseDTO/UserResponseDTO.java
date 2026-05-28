package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String university;
    private String fieldOfStudy;
    private String city;
    private Role role;
    private LocalDateTime createdAt;
}
