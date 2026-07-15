package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.enums.MeetingType;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.CreateStudyGroupRequestDTO;
import com.terangalink.backend.requestDTO.UpdateStudyGroupRequestDTO;
import com.terangalink.backend.responseDTO.StudyGroupResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.StudyGroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudyGroupController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class StudyGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyGroupService studyGroupService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createStudyGroup_shouldReturn201WithLocationAndBody() throws Exception {

        CreateStudyGroupRequestDTO request = createRequest();
        StudyGroupResponseDTO response = sampleResponse(10L);

        when(studyGroupService.createStudyGroup(any(CreateStudyGroupRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/study-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        containsString("/api/study-groups/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Révisions Java"))
                .andExpect(jsonPath("$.meetingType").value("ONLINE"));
    }

    @Test
    void getStudyGroupById_shouldReturn200() throws Exception {

        when(studyGroupService.getStudyGroupById(10L))
                .thenReturn(sampleResponse(10L));

        mockMvc.perform(get("/api/study-groups/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getAllStudyGroups_shouldReturnPage() throws Exception {

        StudyGroupResponseDTO response = sampleResponse(10L);

        when(studyGroupService.getAllStudyGroups(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1));

        mockMvc.perform(get("/api/study-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void searchStudyGroups_shouldReturnPage() throws Exception {

        StudyGroupResponseDTO response = sampleResponse(10L);

        when(studyGroupService.searchStudyGroups(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1));

        mockMvc.perform(get("/api/study-groups/search")
                        .param("title", "Révisions")
                        .param("subject", "Mathématiques")
                        .param("city", "Dakar")
                        .param("meetingType", "ONLINE")
                        .param("available", "true")
                        .param("meetingDate", "2026-08-01T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Révisions Java"))
                .andExpect(jsonPath("$.content[0].meetingType").value("ONLINE"));
    }

    @Test
    void updateStudyGroup_shouldReturn200() throws Exception {

        UpdateStudyGroupRequestDTO request = updateRequest();
        StudyGroupResponseDTO response = sampleResponse(10L);
        response.setTitle("Révisions Java Avancées");

        when(studyGroupService.updateStudyGroup(
                eq(10L),
                any(UpdateStudyGroupRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/study-groups/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Révisions Java Avancées"));
    }

    @Test
    void deleteStudyGroup_shouldReturn204() throws Exception {

        doNothing().when(studyGroupService)
                .deleteStudyGroup(10L);

        mockMvc.perform(delete("/api/study-groups/10"))
                .andExpect(status().isNoContent());
    }

    private CreateStudyGroupRequestDTO createRequest() {

        CreateStudyGroupRequestDTO dto =
                new CreateStudyGroupRequestDTO();

        dto.setTitle("Révisions Java");
        dto.setSubject("Mathématiques");
        dto.setDescription("Séance de révision pour les examens.");
        dto.setCity("Dakar");
        dto.setLocation("Bibliothèque");
        dto.setMeetingType(MeetingType.ONLINE);
        dto.setMeetingDate(LocalDateTime.of(2026, 8, 1, 10, 0));
        dto.setMaxMembers(8);
        dto.setAvailable(true);

        return dto;
    }

    private UpdateStudyGroupRequestDTO updateRequest() {

        UpdateStudyGroupRequestDTO dto =
                new UpdateStudyGroupRequestDTO();

        dto.setTitle("Révisions Java Avancées");
        dto.setMaxMembers(10);

        return dto;
    }

    private StudyGroupResponseDTO sampleResponse(Long id) {

        StudyGroupResponseDTO dto =
                new StudyGroupResponseDTO();

        dto.setId(id);
        dto.setTitle("Révisions Java");
        dto.setSubject("Mathématiques");
        dto.setDescription("Séance de révision pour les examens.");
        dto.setCity("Dakar");
        dto.setLocation("Bibliothèque");
        dto.setMeetingType(MeetingType.ONLINE);
        dto.setMeetingDate(LocalDateTime.of(2026, 8, 1, 10, 0));
        dto.setMaxMembers(8);
        dto.setAvailable(true);
        dto.setCreatorId(1L);
        dto.setCreatorFirstName("Alice");
        dto.setCreatorLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        dto.setUpdatedAt(LocalDateTime.of(2025, 2, 1, 10, 0));

        return dto;
    }
}
