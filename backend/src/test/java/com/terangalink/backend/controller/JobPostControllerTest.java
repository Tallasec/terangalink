package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.enums.ContractType;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.CreateJobPostRequestDTO;
import com.terangalink.backend.requestDTO.UpdateJobPostRequestDTO;
import com.terangalink.backend.responseDTO.JobPostResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.JobPostService;
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

import java.math.BigDecimal;
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

@WebMvcTest(controllers = JobPostController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class JobPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobPostService jobPostService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createJobPost_shouldReturn201WithLocationAndBody() throws Exception {

        CreateJobPostRequestDTO request = createRequest();
        JobPostResponseDTO response = sampleResponse(10L);

        when(jobPostService.createJobPost(any(CreateJobPostRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        containsString("/api/jobs/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Developpeur Java Spring Boot"))
                .andExpect(jsonPath("$.companyName").value("TerangaLink"))
                .andExpect(jsonPath("$.contractType").value("CDI"));
    }

    @Test
    void getJobPostById_shouldReturn200() throws Exception {

        when(jobPostService.getJobPostById(10L))
                .thenReturn(sampleResponse(10L));

        mockMvc.perform(get("/api/jobs/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getAllJobPosts_shouldReturnPage() throws Exception {

        JobPostResponseDTO response = sampleResponse(10L);

        when(jobPostService.getAllJobPosts(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1));

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void searchJobPosts_shouldReturnPage() throws Exception {

        JobPostResponseDTO response = sampleResponse(10L);

        when(jobPostService.searchJobPosts(
                any(),
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

        mockMvc.perform(get("/api/jobs/search")
                        .param("title", "Developpeur")
                        .param("city", "Dakar")
                        .param("companyName", "TerangaLink")
                        .param("contractType", "CDI")
                        .param("salaryMin", "500")
                        .param("salaryMax", "1500")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("TerangaLink"))
                .andExpect(jsonPath("$.content[0].contractType").value("CDI"));
    }

    @Test
    void updateJobPost_shouldReturn200() throws Exception {

        UpdateJobPostRequestDTO request = updateRequest();
        JobPostResponseDTO response = sampleResponse(10L);
        response.setTitle("Developpeur Senior Java");

        when(jobPostService.updateJobPost(
                eq(10L),
                any(UpdateJobPostRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/jobs/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Developpeur Senior Java"));
    }

    @Test
    void deleteJobPost_shouldReturn204() throws Exception {

        doNothing().when(jobPostService)
                .deleteJobPost(10L);

        mockMvc.perform(delete("/api/jobs/10"))
                .andExpect(status().isNoContent());
    }

    private CreateJobPostRequestDTO createRequest() {

        CreateJobPostRequestDTO dto =
                new CreateJobPostRequestDTO();

        dto.setTitle("Developpeur Java Spring Boot");
        dto.setDescription("Poste backend chez TerangaLink.");
        dto.setCompanyName("TerangaLink");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContractType(ContractType.CDI);
        dto.setSalary(new BigDecimal("1200.00"));
        dto.setAvailable(true);

        return dto;
    }

    private UpdateJobPostRequestDTO updateRequest() {

        UpdateJobPostRequestDTO dto =
                new UpdateJobPostRequestDTO();

        dto.setTitle("Developpeur Senior Java");
        dto.setSalary(new BigDecimal("1500.00"));

        return dto;
    }

    private JobPostResponseDTO sampleResponse(Long id) {

        JobPostResponseDTO dto =
                new JobPostResponseDTO();

        dto.setId(id);
        dto.setTitle("Developpeur Java Spring Boot");
        dto.setDescription("Poste backend chez TerangaLink.");
        dto.setCompanyName("TerangaLink");
        dto.setCity("Dakar");
        dto.setAddress("Plateau");
        dto.setContractType(ContractType.CDI);
        dto.setSalary(new BigDecimal("1200.00"));
        dto.setAvailable(true);
        dto.setOwnerId(1L);
        dto.setOwnerFirstName("Alice");
        dto.setOwnerLastName("Dupont");
        dto.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        dto.setUpdatedAt(LocalDateTime.of(2025, 2, 1, 10, 0));

        return dto;
    }
}
