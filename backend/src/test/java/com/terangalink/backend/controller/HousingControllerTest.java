package com.terangalink.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.enums.HousingType;
import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.requestDTO.CreateHousingRequestDTO;
import com.terangalink.backend.requestDTO.UpdateHousingRequestDTO;
import com.terangalink.backend.responseDTO.HousingResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.HousingService;
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

@WebMvcTest(controllers = HousingController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class HousingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HousingService housingService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createHousing_shouldReturn201WithLocationAndBody() throws Exception {
        CreateHousingRequestDTO request = validCreateRequest();
        HousingResponseDTO response = sampleHousingResponse(10L);

        when(housingService.createHousing(any(CreateHousingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/housings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/housings/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Studio meuble proche campus"))
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.price").value(750.00))
                .andExpect(jsonPath("$.housingType").value("STUDIO"))
                .andExpect(jsonPath("$.ownerId").value(1));
    }

    @Test
    void getHousingById_shouldReturn200() throws Exception {
        when(housingService.getHousingById(10L)).thenReturn(sampleHousingResponse(10L));

        mockMvc.perform(get("/api/housings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Studio meuble proche campus"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getHousingsPage_shouldReturn200WithPagedJson() throws Exception {
        HousingResponseDTO response = sampleHousingResponse(10L);
        when(housingService.getAllHousings(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/housings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].city").value("Paris"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void patchHousing_shouldReturn200WithUpdatedBody() throws Exception {
        UpdateHousingRequestDTO request = validUpdateRequest();
        HousingResponseDTO response = sampleHousingResponse(10L);
        response.setTitle("Studio meuble renove");
        response.setPrice(new BigDecimal("780.00"));

        when(housingService.updateHousing(eq(10L), any(UpdateHousingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/housings/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Studio meuble renove"))
                .andExpect(jsonPath("$.price").value(780.00));
    }

    @Test
    void deleteHousing_shouldReturn204() throws Exception {
        doNothing().when(housingService).deleteHousing(10L);

        mockMvc.perform(delete("/api/housings/10"))
                .andExpect(status().isNoContent());
    }

    private CreateHousingRequestDTO validCreateRequest() {
        CreateHousingRequestDTO request = new CreateHousingRequestDTO();
        request.setTitle("Studio meuble proche campus");
        request.setDescription("Studio lumineux et calme.");
        request.setCity("Paris");
        request.setAddress("12 rue des Etudiants");
        request.setPrice(new BigDecimal("750.00"));
        request.setHousingType(HousingType.STUDIO);
        request.setAvailable(true);
        return request;
    }

    private UpdateHousingRequestDTO validUpdateRequest() {
        UpdateHousingRequestDTO request = new UpdateHousingRequestDTO();
        request.setTitle("Studio meuble renove");
        request.setPrice(new BigDecimal("780.00"));
        return request;
    }

    private HousingResponseDTO sampleHousingResponse(Long id) {
        HousingResponseDTO response = new HousingResponseDTO();
        response.setId(id);
        response.setTitle("Studio meuble proche campus");
        response.setDescription("Studio lumineux et calme.");
        response.setCity("Paris");
        response.setAddress("12 rue des Etudiants");
        response.setPrice(new BigDecimal("750.00"));
        response.setHousingType(HousingType.STUDIO);
        response.setAvailable(true);
        response.setOwnerId(1L);
        response.setOwnerFirstName("Alice");
        response.setOwnerLastName("Dupont");
        response.setCreatedAt(LocalDateTime.of(2025, 2, 1, 9, 0));
        return response;
    }
    @Test
    void searchHousings_shouldReturn200WithPagedJson() throws Exception {

        HousingResponseDTO response = sampleHousingResponse(10L);

        when(housingService.searchHousings(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(PageRequest.class)))
                .thenReturn(
                        new PageImpl<>(
                                List.of(response),
                                PageRequest.of(0, 20),
                                1
                        )
                );

        mockMvc.perform(get("/api/housings/search")
                        .param("city", "Paris")
                        .param("housingType", "STUDIO")
                        .param("available", "true")
                        .param("minPrice", "500")
                        .param("maxPrice", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("Paris"))
                .andExpect(jsonPath("$.content[0].housingType").value("STUDIO"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
