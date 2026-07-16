package com.terangalink.backend.service;

import com.terangalink.backend.entity.Association;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.AssociationType;
import com.terangalink.backend.exception.business.AssociationNotFoundException;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.AssociationMapper;
import com.terangalink.backend.repository.AssociationRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateAssociationRequestDTO;
import com.terangalink.backend.requestDTO.UpdateAssociationRequestDTO;
import com.terangalink.backend.responseDTO.AssociationResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.specification.AssociationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
ASSOCIATION SERVICE

Gere les operations metier
liees aux associations.
*/

@Service
@Transactional(readOnly = true)
public class AssociationService {

    private final AssociationRepository associationRepository;
    private final UserRepository userRepository;
    private final AssociationMapper associationMapper;

    public AssociationService(
            AssociationRepository associationRepository,
            UserRepository userRepository,
            AssociationMapper associationMapper
    ) {
        this.associationRepository = associationRepository;
        this.userRepository = userRepository;
        this.associationMapper = associationMapper;
    }

    // Creation d'une association
    @Transactional
    public AssociationResponseDTO createAssociation(
            CreateAssociationRequestDTO request
    ) {

        UserPrincipal principal = getCurrentPrincipal();

        User creator = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));

        Association association = associationMapper.toEntity(request);

        association.setCreator(creator);

        association = associationRepository.save(association);

        return associationMapper.toResponseDto(association);
    }

    // Recuperation d'une association
    public AssociationResponseDTO getAssociationById(Long id) {

        Association association = findAssociationByIdOrThrow(id);

        return associationMapper.toResponseDto(association);
    }

    // Liste des associations
    public Page<AssociationResponseDTO> getAllAssociations(Pageable pageable) {

        Specification<Association> specification =
                AssociationSpecification.build(
                        null,
                        null,
                        null,
                        null
                );

        return associationRepository.findAll(specification, pageable)
                .map(associationMapper::toResponseDto);
    }

    // Recherche dynamique des associations
    public Page<AssociationResponseDTO> searchAssociations(
            String title,
            String city,
            AssociationType associationType,
            Boolean available,
            Pageable pageable
    ) {

        Specification<Association> specification =
                AssociationSpecification.build(
                        title,
                        city,
                        associationType,
                        available
                );

        return associationRepository.findAll(specification, pageable)
                .map(associationMapper::toResponseDto);
    }

    // Modification d'une association
    @Transactional
    public AssociationResponseDTO updateAssociation(
            Long id,
            UpdateAssociationRequestDTO request
    ) {

        Association association = findAssociationByIdOrThrow(id);

        associationMapper.updateEntity(association, request);

        association = associationRepository.save(association);

        return associationMapper.toResponseDto(association);
    }

    // Suppression logique d'une association
    @Transactional
    public void deleteAssociation(Long id) {

        Association association = findAssociationByIdOrThrow(id);

        association.setDeleted(true);
        associationRepository.save(association);
    }

    // Recuperer l'utilisateur authentifie
    private UserPrincipal getCurrentPrincipal() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {

            throw new InvalidCredentialsException(
                    "Utilisateur non authentifie.");
        }

        return principal;
    }

    // Recherche une association par son identifiant
    private Association findAssociationByIdOrThrow(Long id) {

        return associationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new AssociationNotFoundException(
                                "Association introuvable avec l'id : " + id));
    }
}
