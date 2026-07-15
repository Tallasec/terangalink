package com.terangalink.backend.responseDTO;

import com.terangalink.backend.enums.MeetingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
STUDY GROUP RESPONSE DTO

Représente les informations
retournées au client.
*/

@Getter
@Setter
@NoArgsConstructor

public class StudyGroupResponseDTO {

    private Long id;

    private String title;

    private String subject;

    private String description;

    private String city;

    private String location;

    private MeetingType meetingType;

    private LocalDateTime meetingDate;

    private Integer maxMembers;

    private boolean available;

    private Long creatorId;

    private String creatorFirstName;

    private String creatorLastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
