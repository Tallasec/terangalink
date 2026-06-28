package com.terangalink.backend.controller;

import com.terangalink.backend.exception.GlobalExceptionHandler;
import com.terangalink.backend.responseDTO.HousingImageResponseDTO;
import com.terangalink.backend.security.CustomUserDetailsService;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.service.HousingImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HousingImageController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class HousingImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HousingImageService housingImageService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void uploadImages_shouldReturn201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "photo.jpg", "image/jpeg", "image".getBytes());
        HousingImageResponseDTO response = sampleImageResponse();

        when(housingImageService.uploadImages(eq(10L), anyList())).thenReturn(List.of(response));

        mockMvc.perform(multipart("/api/housings/10/images").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].imageUrl").value("https://res.cloudinary.com/demo/photo.jpg"));
    }

    @Test
    void getImagesByHousing_shouldReturn200() throws Exception {
        when(housingImageService.getImagesByHousing(10L)).thenReturn(List.of(sampleImageResponse()));

        mockMvc.perform(get("/api/housings/10/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].imageUrl").value("https://res.cloudinary.com/demo/photo.jpg"));
    }

    @Test
    void deleteImage_shouldReturn204() throws Exception {
        doNothing().when(housingImageService).deleteImage(20L);

        mockMvc.perform(delete("/api/housing-images/20"))
                .andExpect(status().isNoContent());
    }

    private HousingImageResponseDTO sampleImageResponse() {
        HousingImageResponseDTO response = new HousingImageResponseDTO();
        response.setId(20L);
        response.setImageUrl("https://res.cloudinary.com/demo/photo.jpg");
        return response;
    }
}
