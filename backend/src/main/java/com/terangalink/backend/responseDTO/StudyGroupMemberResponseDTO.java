package com.terangalink.backend.responseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StudyGroupMemberResponseDTO {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String university;
    private String fieldOfStudy;
    private String city;
    private LocalDateTime joinedAt;
}
