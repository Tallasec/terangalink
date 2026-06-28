package com.terangalink.backend.service;

import com.terangalink.backend.entity.HousingImage;
import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.HousingType;
import com.terangalink.backend.exception.business.HousingImageNotFoundException;
import com.terangalink.backend.exception.business.HousingNotFoundException;
import com.terangalink.backend.repository.HousingImageRepository;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.responseDTO.HousingImageResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.support.AuthTestFixtures;
import com.terangalink.backend.support.UserTestFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HousingImageServiceTest {

    @Mock
    private HousingRepository housingRepository;

    @Mock
    private HousingImageRepository housingImageRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void uploadImages_shouldUploadAndSaveImages() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housing = sampleHousing(10L, owner);
        MockMultipartFile file = imageFile("photo.jpg");
        HousingImage savedImage = sampleImage(20L, housing);

        authenticate(UserPrincipal.from(owner));
        when(housingRepository.findById(10L)).thenReturn(Optional.of(housing));
        when(housingImageRepository.countByHousingId(10L)).thenReturn(0L);
        when(cloudinaryService.uploadImage(file))
                .thenReturn(new CloudinaryService.UploadResult("housing/photo", "https://res.cloudinary.com/demo/photo.jpg"));
        when(housingImageRepository.save(any(HousingImage.class))).thenReturn(savedImage);

        List<HousingImageResponseDTO> result = service.uploadImages(10L, List.of(file));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(20L);
        assertThat(result.get(0).getImageUrl()).isEqualTo("https://res.cloudinary.com/demo/photo.jpg");
        verify(cloudinaryService).uploadImage(file);
        verify(housingImageRepository).save(any(HousingImage.class));
    }

    @Test
    void uploadImages_shouldThrowWhenHousingDoesNotExist() {
        HousingImageService service = service();
        authenticate(AuthTestFixtures.sampleUserPrincipal(1L));
        when(housingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.uploadImages(99L, List.of(imageFile("photo.jpg"))))
                .isInstanceOf(HousingNotFoundException.class)
                .hasMessage("Logement introuvable avec l'id : 99");

        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void uploadImages_shouldRejectInvalidFormat() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);

        authenticate(UserPrincipal.from(owner));
        when(housingRepository.findById(10L)).thenReturn(Optional.of(sampleHousing(10L, owner)));
        when(housingImageRepository.countByHousingId(10L)).thenReturn(0L);

        assertThatThrownBy(() -> service.uploadImages(10L, List.of(imageFile("photo.gif"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Format d'image invalide");

        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void uploadImages_shouldRejectFileLargerThanFiveMb() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);
        byte[] content = new byte[(5 * 1024 * 1024) + 1];
        MockMultipartFile file = new MockMultipartFile("files", "photo.png", "image/png", content);

        authenticate(UserPrincipal.from(owner));
        when(housingRepository.findById(10L)).thenReturn(Optional.of(sampleHousing(10L, owner)));
        when(housingImageRepository.countByHousingId(10L)).thenReturn(0L);

        assertThatThrownBy(() -> service.uploadImages(10L, List.of(file)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La taille de chaque image ne doit pas depasser 5 MB.");

        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void uploadImages_shouldRejectWhenHousingWouldExceedTenImages() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);

        authenticate(UserPrincipal.from(owner));
        when(housingRepository.findById(10L)).thenReturn(Optional.of(sampleHousing(10L, owner)));
        when(housingImageRepository.countByHousingId(10L)).thenReturn(10L);

        assertThatThrownBy(() -> service.uploadImages(10L, List.of(imageFile("photo.webp"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un logement ne peut pas avoir plus de 10 photos.");

        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void deleteImage_shouldDeleteFromCloudinaryAndRepository() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housing = sampleHousing(10L, owner);
        HousingImage image = sampleImage(20L, housing);

        authenticate(UserPrincipal.from(owner));
        when(housingImageRepository.findById(20L)).thenReturn(Optional.of(image));

        service.deleteImage(20L);

        verify(cloudinaryService).deleteImage("housing/photo");
        verify(housingImageRepository).delete(image);
    }

    @Test
    void deleteImage_shouldThrowWhenImageDoesNotExist() {
        HousingImageService service = service();
        when(housingImageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteImage(99L))
                .isInstanceOf(HousingImageNotFoundException.class)
                .hasMessage("Image de logement introuvable avec l'id : 99");

        verify(cloudinaryService, never()).deleteImage(any());
    }

    @Test
    void deleteImage_shouldRejectOtherUser() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);
        HousingImage image = sampleImage(20L, sampleHousing(10L, owner));

        authenticate(AuthTestFixtures.sampleUserPrincipal(2L));
        when(housingImageRepository.findById(20L)).thenReturn(Optional.of(image));

        assertThatThrownBy(() -> service.deleteImage(20L))
                .isInstanceOf(AccessDeniedException.class);

        verify(cloudinaryService, never()).deleteImage(any());
    }

    @Test
    void getImagesByHousing_shouldReturnImages() {
        HousingImageService service = service();
        User owner = UserTestFixtures.sampleUser(1L);
        HousingPost housing = sampleHousing(10L, owner);
        HousingImage image = sampleImage(20L, housing);

        when(housingRepository.findById(10L)).thenReturn(Optional.of(housing));
        when(housingImageRepository.findByHousingId(10L)).thenReturn(List.of(image));

        List<HousingImageResponseDTO> result = service.getImagesByHousing(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageUrl()).isEqualTo("https://res.cloudinary.com/demo/photo.jpg");
    }

    private HousingImageService service() {
        return new HousingImageService(housingRepository, housingImageRepository, cloudinaryService);
    }

    private void authenticate(UserPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private MockMultipartFile imageFile(String filename) {
        return new MockMultipartFile("files", filename, "image/jpeg", "image".getBytes());
    }

    private HousingPost sampleHousing(Long id, User owner) {
        HousingPost housing = new HousingPost();
        housing.setId(id);
        housing.setTitle("Studio meuble proche campus");
        housing.setCity("Paris");
        housing.setPrice(new BigDecimal("750.00"));
        housing.setHousingType(HousingType.STUDIO);
        housing.setAvailable(true);
        housing.setOwner(owner);
        return housing;
    }

    private HousingImage sampleImage(Long id, HousingPost housing) {
        HousingImage image = new HousingImage();
        image.setId(id);
        image.setPublicId("housing/photo");
        image.setImageUrl("https://res.cloudinary.com/demo/photo.jpg");
        image.setHousing(housing);
        return image;
    }
}
