package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.enums.ForumCategory;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.CreateForumTopicRequestDTO;
import com.terangalink.backend.requestDTO.UpdateForumTopicRequestDTO;
import com.terangalink.backend.responseDTO.ForumTopicResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.ForumTopicService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ForumTopicController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ForumTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ForumTopicService forumTopicService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createForumTopic_shouldReturn201() throws Exception {

        CreateForumTopicRequestDTO request = createRequest();

        ForumTopicResponseDTO response = sampleResponse(1L);

        when(forumTopicService.createForumTopic(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/forum/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        containsString("/api/forum/topics/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Comment trouver un logement ?"))
                .andExpect(jsonPath("$.category").value("LOGEMENT"));
    }

    @Test
    void getForumTopicById_shouldReturn200() throws Exception {

        when(forumTopicService.getForumTopicById(1L))
                .thenReturn(sampleResponse(1L));

        mockMvc.perform(get("/api/forum/topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.views").value(5));
    }

    @Test
    void getAllForumTopics_shouldReturnPage() throws Exception {

        ForumTopicResponseDTO response = sampleResponse(1L);

        when(forumTopicService.getAllForumTopics(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0,20),
                        1));

        mockMvc.perform(get("/api/forum/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }
    @Test
    void searchForumTopics_shouldReturnPage() throws Exception {

        ForumTopicResponseDTO response = sampleResponse(1L);

        when(forumTopicService.searchForumTopics(
                any(),
                any(),
                any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0,20),
                        1));

        mockMvc.perform(get("/api/forum/topics/search")
                        .param("title", "logement")
                        .param("category", "LOGEMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].category").value("LOGEMENT"));
    }

    @Test
    void updateForumTopic_shouldReturn200() throws Exception {

        UpdateForumTopicRequestDTO request = updateRequest();

        ForumTopicResponseDTO response = sampleResponse(1L);
        response.setTitle("Titre modifié");

        when(forumTopicService.updateForumTopic(
                eq(1L),
                any(UpdateForumTopicRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/forum/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Titre modifié"));
    }

    @Test
    void deleteForumTopic_shouldReturn204() throws Exception {

        doNothing().when(forumTopicService)
                .deleteForumTopic(1L);

        mockMvc.perform(delete("/api/forum/topics/1"))
                .andExpect(status().isNoContent());
    }

    private CreateForumTopicRequestDTO createRequest() {

        CreateForumTopicRequestDTO dto =
                new CreateForumTopicRequestDTO();

        dto.setTitle("Comment trouver un logement ?");
        dto.setContent("Je cherche un studio.");
        dto.setCategory(ForumCategory.LOGEMENT);

        return dto;
    }

    private UpdateForumTopicRequestDTO updateRequest() {

        UpdateForumTopicRequestDTO dto =
                new UpdateForumTopicRequestDTO();

        dto.setTitle("Titre modifié");
        dto.setContent("Contenu modifié");
        dto.setCategory(ForumCategory.LOGEMENT);

        return dto;
    }

    private ForumTopicResponseDTO sampleResponse(Long id) {

        ForumTopicResponseDTO dto =
                new ForumTopicResponseDTO();

        dto.setId(id);
        dto.setTitle("Comment trouver un logement ?");
        dto.setContent("Je cherche un studio.");
        dto.setCategory(ForumCategory.LOGEMENT);
        dto.setAuthorId(1L);
        dto.setAuthorFirstName("Talla");
        dto.setAuthorLastName("Seck");
        dto.setViews(5L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        return dto;
    }

}