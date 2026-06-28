package com.terangalink.backend.service;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.HousingType;
import com.terangalink.backend.exception.business.HousingNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.HousingMapper;
import com.terangalink.backend.repository.HousingRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateHousingRequestDTO;
import com.terangalink.backend.requestDTO.UpdateHousingRequestDTO;
import com.terangalink.backend.responseDTO.HousingResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.specification.HousingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class HousingService {

    private final HousingRepository housingRepository;
    private final UserRepository userRepository;
    private final HousingMapper housingMapper;

    public HousingService(
            HousingRepository housingRepository,
            UserRepository userRepository,
            HousingMapper housingMapper) {
        this.housingRepository = housingRepository;
        this.userRepository = userRepository;
        this.housingMapper = housingMapper;
    }

    @Transactional
    public HousingResponseDTO createHousing(CreateHousingRequestDTO request) {
        UserPrincipal principal = getCurrentPrincipal();
        User owner = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));

        HousingPost housingPost = housingMapper.toEntity(request);
        housingPost.setOwner(owner);

        return housingMapper.toResponseDto(housingRepository.save(housingPost));
    }

    public HousingResponseDTO getHousingById(Long id) {
        return housingMapper.toResponseDto(findHousingByIdOrThrow(id));
    }

    public Page<HousingResponseDTO> getAllHousings(Pageable pageable) {
        return housingRepository.findAll(pageable)
                .map(housingMapper::toResponseDto);
    }

    @Transactional
    public HousingResponseDTO updateHousing(Long id, UpdateHousingRequestDTO request) {
        HousingPost housingPost = findHousingByIdOrThrow(id);
        housingMapper.updateEntity(housingPost, request);
        housingPost.setUpdatedAt(LocalDateTime.now());

        return housingMapper.toResponseDto(housingRepository.save(housingPost));
    }

    @Transactional
    public void deleteHousing(Long id) {
        HousingPost housingPost = findHousingByIdOrThrow(id);
        housingRepository.delete(housingPost);
    }

    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("Utilisateur non authentifie.");
        }

        return principal;
    }

    private HousingPost findHousingByIdOrThrow(Long id) {
        return housingRepository.findById(id)
                .orElseThrow(() -> new HousingNotFoundException(
                        "Logement introuvable avec l'id : " + id));
    }

    // Recherche des logements avec filtres
    public Page<HousingResponseDTO> searchHousings(
            String city,
            HousingType housingType,
            Boolean available,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        Specification<HousingPost> specification = HousingSpecification.build(
                city,
                housingType,
                available,
                minPrice,
                maxPrice
        );

        return housingRepository
                .findAll(specification, pageable)
                .map(housingMapper::toResponseDto);
    }
}
