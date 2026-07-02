package com.terangalink.backend.controller;

import com.terangalink.backend.enums.HousingType;
import com.terangalink.backend.requestDTO.CreateHousingRequestDTO;
import com.terangalink.backend.requestDTO.UpdateHousingRequestDTO;
import com.terangalink.backend.responseDTO.HousingResponseDTO;
import com.terangalink.backend.service.HousingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/api/housings")
public class HousingController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "city", "price", "housingType", "available", "createdAt", "updatedAt");

    private final HousingService housingService;

    public HousingController(HousingService housingService) {
        this.housingService = housingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<HousingResponseDTO> createHousing(
            @RequestBody @Valid CreateHousingRequestDTO request) {
        HousingResponseDTO createdHousing = housingService.createHousing(request);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(createdHousing.getId())
                        .toUri())
                .body(createdHousing);
    }

    @GetMapping
    public ResponseEntity<Page<HousingResponseDTO>> getHousingsPage(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Le numero de page doit etre superieur ou egal a 0.") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "La taille de page doit etre au moins 1.")
            @Max(value = 100, message = "La taille de page ne doit pas depasser 100.") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort.length > 0 ? sort[0] : "id";
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("Le champ de tri '" + sortField + "' n'est pas autorise.");
        }
        Sort.Direction direction = sort.length > 1 && "desc".equalsIgnoreCase(sort[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortField));
        return ResponseEntity.ok(housingService.getAllHousings(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousingResponseDTO> getHousingById(@PathVariable Long id) {
        return ResponseEntity.ok(housingService.getHousingById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@housingSecurityService.canAccessHousing(#id)")
    public ResponseEntity<HousingResponseDTO> patchHousing(
            @PathVariable Long id,
            @RequestBody @Valid UpdateHousingRequestDTO request) {
        return ResponseEntity.ok(housingService.updateHousing(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@housingSecurityService.canAccessHousing(#id)")
    public ResponseEntity<Void> deleteHousing(@PathVariable Long id) {
        housingService.deleteHousing(id);
        return ResponseEntity.noContent().build();
    }

    // Recherche dynamique des logements
    @GetMapping("/search")
    public ResponseEntity<Page<HousingResponseDTO>> searchHousings(

            @RequestParam(required = false) String city,

            @RequestParam(required = false) HousingType housingType,

            @RequestParam(required = false) Boolean available,

            @RequestParam(required = false) BigDecimal minPrice,

            @RequestParam(required = false) BigDecimal maxPrice,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Le numero de page doit etre superieur ou egal a 0.")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "La taille de page doit etre au moins 1.")
            @Max(value = 100, message = "La taille de page ne doit pas depasser 100.")
            int size,

            @RequestParam(defaultValue = "createdAt,desc")
            String[] sort
    ) {

        String sortField = sort.length > 0 ? sort[0] : "createdAt";

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "Le champ de tri '" + sortField + "' n'est pas autorise.");
        }

        Sort.Direction direction =
                sort.length > 1 && "desc".equalsIgnoreCase(sort[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        return ResponseEntity.ok(
                housingService.searchHousings(
                        city,
                        housingType,
                        available,
                        minPrice,
                        maxPrice,
                        pageable
                )
        );
    }
}
