package com.terangalink.backend.service;

import com.terangalink.backend.entity.HousingImage;
import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.exception.business.HousingImageNotFoundException;
import com.terangalink.backend.exception.business.HousingNotFoundException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.repository.HousingImageRepository;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.responseDTO.HousingImageResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class HousingImageService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final int MAX_IMAGES_PER_HOUSING = 10;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final HousingRepository housingRepository;
    private final HousingImageRepository housingImageRepository;
    private final CloudinaryService cloudinaryService;

    public HousingImageService(
            HousingRepository housingRepository,
            HousingImageRepository housingImageRepository,
            CloudinaryService cloudinaryService) {
        this.housingRepository = housingRepository;
        this.housingImageRepository = housingImageRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public List<HousingImageResponseDTO> uploadImages(Long housingId, List<MultipartFile> files) {
        HousingPost housing = findHousingByIdOrThrow(housingId);
        ensureCanManage(housing);
        validateFiles(housingId, files);

        return files.stream()
                .map(file -> uploadAndSaveImage(housing, file))
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public void deleteImage(Long imageId) {
        HousingImage image = housingImageRepository.findById(imageId)
                .orElseThrow(() -> new HousingImageNotFoundException(
                        "Image de logement introuvable avec l'id : " + imageId));

        ensureCanManage(image.getHousing());
        cloudinaryService.deleteImage(image.getPublicId());
        housingImageRepository.delete(image);
    }

    public List<HousingImageResponseDTO> getImagesByHousing(Long housingId) {
        findHousingByIdOrThrow(housingId);
        return housingImageRepository.findByHousingId(housingId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    private HousingImage uploadAndSaveImage(HousingPost housing, MultipartFile file) {
        CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadImage(file);

        HousingImage image = new HousingImage();
        image.setHousing(housing);
        image.setPublicId(uploadResult.publicId());
        image.setImageUrl(uploadResult.imageUrl());

        return housingImageRepository.save(image);
    }

    private void validateFiles(Long housingId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Au moins une image est obligatoire.");
        }

        long existingImages = housingImageRepository.countByHousingId(housingId);
        if (existingImages + files.size() > MAX_IMAGES_PER_HOUSING) {
            throw new IllegalArgumentException("Un logement ne peut pas avoir plus de 10 photos.");
        }

        files.forEach(this::validateFile);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier image ne peut pas etre vide.");
        }
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("La taille de chaque image ne doit pas depasser 5 MB.");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Format d'image invalide. Formats autorises : jpg, jpeg, png, webp.");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private void ensureCanManage(HousingPost housing) {
        UserPrincipal principal = getCurrentPrincipal();
        if (principal.getRole() == Role.ADMIN) {
            return;
        }
        if (housing.getOwner() != null
                && housing.getOwner().getId() != null
                && housing.getOwner().getId().equals(principal.getId())) {
            return;
        }
        throw new AccessDeniedException("Acces refuse.");
    }

    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new InvalidCredentialsException("Utilisateur non authentifie.");
        }

        return principal;
    }

    private HousingPost findHousingByIdOrThrow(Long housingId) {
        return housingRepository.findById(housingId)
                .orElseThrow(() -> new HousingNotFoundException(
                        "Logement introuvable avec l'id : " + housingId));
    }

    private HousingImageResponseDTO toResponseDto(HousingImage image) {
        HousingImageResponseDTO dto = new HousingImageResponseDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        return dto;
    }
}
