package com.terangalink.backend.controller;

import com.terangalink.backend.responseDTO.HousingImageResponseDTO;
import com.terangalink.backend.service.HousingImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
public class HousingImageController {

    private final HousingImageService housingImageService;

    public HousingImageController(HousingImageService housingImageService) {
        this.housingImageService = housingImageService;
    }

    @PostMapping(value = "/api/housings/{housingId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@housingImageSecurityService.canManageHousingImages(#housingId)")
    public ResponseEntity<List<HousingImageResponseDTO>> uploadImages(
            @PathVariable Long housingId,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(housingImageService.uploadImages(housingId, files));
    }

    @GetMapping("/api/housings/{housingId}/images")
    public ResponseEntity<List<HousingImageResponseDTO>> getImagesByHousing(@PathVariable Long housingId) {
        return ResponseEntity.ok(housingImageService.getImagesByHousing(housingId));
    }

    @DeleteMapping("/api/housing-images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        housingImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
