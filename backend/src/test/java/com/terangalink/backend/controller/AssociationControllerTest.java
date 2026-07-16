package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.enums.AssociationType;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.CreateAssociationRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAssociationRequestDTO;
import com.terangalink.backend.responseDTO.AssociationResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.AssociationService;
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

@WebMvcTest(controllers = AssociationController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AssociationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssociationService associationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createAssociation_shouldReturn201WithLocationAndBody() throws Exception {

        CreateAssociationRequestDTO request = createRequest();
        AssociationResponseDTO response = sampleResponse(10L);

        when(associationService.createAssociation(any(CreateAssociationRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/associations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        containsString("/api/associations/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("TerangaLink Association"))
                .andExpect(jsonPath("$.associationType").value("DAHIRA"));
    }

    @Test
    void getAssociationById_shouldReturn200() throws Exception {

        when(associationService.getAssociationById(10L))
                .thenReturn(sampleResponse(10L));

        mockMvc.perform(get("/api/associations/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getAllAssociations_shouldReturnPage() throws Exception {

        AssociationResponseDTO response = sampleResponse(10L);

        when(associationService.getAllAssociations(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1));

        mockMvc.perform(get("/api/associations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void searchAssociations_shouldReturnPage() throws Exception {

        AssociationResponseDTO response = sampleResponse(10L);

        when(associationService.searchAssociations(
                any(),
                any(),
                any(),
                any(),
                any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1));

        mockMvc.perform(get("/api/associations/search")
                        .param("title", "TerangaLink")
                        .param("city", "Dakar")
                        .param("associationType", "DAHIRA")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("TerangaLink Association"))
                .andExpect(jsonPath("$.content[0].associationType").value("DAHIRA"));
    }

    @Test
    void updateAssociation_shouldReturn200() throws Exception {

        UpdateAssociationRequestDTO request = updateRequest();
        AssociationResponseDTO response = sampleResponse(10L);
        response.setTitle("TerangaLink Association Renovee");

        when(associationService.updateAssociation(
                eq(10L),
                any(UpdateAssociationRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/associations/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("TerangaLink Association Renovee"));
    }

    @Test
    void deleteAssociation_shouldReturn204() throws Exception {

        doNothing().when(associationService)
                .deleteAssociation(10L);

        mockMvc.perform(delete("/api/associations/10"))
                .andExpect(status().isNoContent());
    }

    private CreateAssociationRequestDTO createRequest() {

        CreateAssociationRequestDTO dto =
                new CreateAssociationRequestDTO();

        dto.setTitle("TerangaLink Association");
        dto.setDescription("Association etudiante et culturelle.");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContactEmail("contact@terangalink.org");
        dto.setPhone("+221770000000");
        dto.setWebsite("https://terangalink.org");
        dto.setLogoUrl("https://terangalink.org/logo.png");
        dto.setAssociationType(AssociationType.DAHIRA);
        dto.setAvailable(true);

        return dto;
    }

    private UpdateAssociationRequestDTO updateRequest() {

        UpdateAssociationRequestDTO dto =
                new UpdateAssociationRequestDTO();

        dto.setTitle("TerangaLink Association Renovee");
        dto.setAvailable(false);

        return dto;
    }

    private AssociationResponseDTO sampleResponse(Long id) {

        AssociationResponseDTO dto =
                new AssociationResponseDTO();

        dto.setId(id);
        dto.setTitle("TerangaLink Association");
        dto.setDescription("Association etudiante et culturelle.");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContactEmail("contact@terangalink.org");
        dto.setPhone("+221770000000");
        dto.setWebsite("https://terangalink.org");
        dto.setLogoUrl("https://terangalink.org/logo.png");
        dto.setAssociationType(AssociationType.DAHIRA);
        dto.setAvailable(true);
        dto.setCreatorId(1L);
        dto.setCreatorFirstName("Alice");
        dto.setCreatorLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        dto.setUpdatedAt(LocalDateTime.of(2025, 2, 1, 10, 0));

        return dto;
    }
}
