package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.CreateAnswerRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAnswerRequestDTO;
import com.terangalink.backend.responseDTO.AnswerResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.AnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(controllers = AnswerController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createAnswer_shouldReturn201WithLocationAndBody() throws Exception {

        CreateAnswerRequestDTO request = createRequest();
        AnswerResponseDTO response = sampleResponse(20L);

        when(answerService.createAnswer(eq(10L), any(CreateAnswerRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/forum/topics/10/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        containsString("/api/forum/topics/10/answers/20")))
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.content").value("Merci pour cette précision."))
                .andExpect(jsonPath("$.forumTopicId").value(10))
                .andExpect(jsonPath("$.authorId").value(1));
    }

    @Test
    void getAnswersByForumTopic_shouldReturn200() throws Exception {

        when(answerService.getAnswersByForumTopic(10L))
                .thenReturn(List.of(sampleResponse(20L)));

        mockMvc.perform(get("/api/forum/topics/10/answers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].forumTopicId").value(10));
    }

    @Test
    void getAnswerById_shouldReturn200() throws Exception {

        when(answerService.getAnswerById(20L))
                .thenReturn(sampleResponse(20L));

        mockMvc.perform(get("/api/forum/answers/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.authorFirstName").value("Alice"));
    }

    @Test
    void patchAnswer_shouldReturn200() throws Exception {

        UpdateAnswerRequestDTO request = updateRequest();
        AnswerResponseDTO response = sampleResponse(20L);
        response.setContent("Contenu modifié");

        when(answerService.updateAnswer(
                eq(20L),
                any(UpdateAnswerRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/forum/answers/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.content").value("Contenu modifié"));
    }

    @Test
    void deleteAnswer_shouldReturn204() throws Exception {

        doNothing().when(answerService)
                .deleteAnswer(20L);

        mockMvc.perform(delete("/api/forum/answers/20"))
                .andExpect(status().isNoContent());
    }

    private CreateAnswerRequestDTO createRequest() {

        CreateAnswerRequestDTO dto =
                new CreateAnswerRequestDTO();

        dto.setContent("Merci pour cette précision.");

        return dto;
    }

    private UpdateAnswerRequestDTO updateRequest() {

        UpdateAnswerRequestDTO dto =
                new UpdateAnswerRequestDTO();

        dto.setContent("Contenu modifié");

        return dto;
    }

    private AnswerResponseDTO sampleResponse(Long id) {

        AnswerResponseDTO dto =
                new AnswerResponseDTO();

        dto.setId(id);
        dto.setContent("Merci pour cette précision.");
        dto.setForumTopicId(10L);
        dto.setAuthorId(1L);
        dto.setAuthorFirstName("Alice");
        dto.setAuthorLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        dto.setUpdatedAt(LocalDateTime.of(2025, 2, 1, 10, 0));

        return dto;
    }
}
