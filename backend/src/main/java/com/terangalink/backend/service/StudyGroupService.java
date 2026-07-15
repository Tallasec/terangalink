package com.terangalink.backend.service;

import com.terangalink.backend.entity.StudyGroup;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.MeetingType;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.StudyGroupNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.StudyGroupMapper;
import com.terangalink.backend.repository.StudyGroupRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateStudyGroupRequestDTO;
import com.terangalink.backend.requestDTO.UpdateStudyGroupRequestDTO;
import com.terangalink.backend.responseDTO.StudyGroupResponseDTO;
import com.terangalink.backend.security.UserPrincipal;
import com.terangalink.backend.specification.StudyGroupSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
STUDY GROUP SERVICE

Gère les opérations métier
liées aux groupes de révision.
*/

@Service
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;
    private final StudyGroupMapper studyGroupMapper;

    public StudyGroupService(
            StudyGroupRepository studyGroupRepository,
            UserRepository userRepository,
            StudyGroupMapper studyGroupMapper
    ) {
        this.studyGroupRepository = studyGroupRepository;
        this.userRepository = userRepository;
        this.studyGroupMapper = studyGroupMapper;
    }

    // Création d'un groupe
    @Transactional
    public StudyGroupResponseDTO createStudyGroup(
            CreateStudyGroupRequestDTO request
    ) {

        UserPrincipal principal = getCurrentPrincipal();

        User creator = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));

        StudyGroup studyGroup = studyGroupMapper.toEntity(request);

        studyGroup.setCreator(creator);

        studyGroup = studyGroupRepository.save(studyGroup);

        return studyGroupMapper.toResponseDto(studyGroup);
    }

    // Récupération d'un groupe
    public StudyGroupResponseDTO getStudyGroupById(Long id) {

        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);

        return studyGroupMapper.toResponseDto(studyGroup);
    }

    // Liste des groupes
    public Page<StudyGroupResponseDTO> getAllStudyGroups(Pageable pageable) {

        Specification<StudyGroup> specification =
                StudyGroupSpecification.build(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        return studyGroupRepository.findAll(specification, pageable)
                .map(studyGroupMapper::toResponseDto);
    }

    // Recherche dynamique des groupes
    public Page<StudyGroupResponseDTO> searchStudyGroups(
            String title,
            String subject,
            String city,
            MeetingType meetingType,
            Boolean available,
            LocalDateTime meetingDate,
            Pageable pageable
    ) {

        Specification<StudyGroup> specification =
                StudyGroupSpecification.build(
                        title,
                        subject,
                        city,
                        meetingType,
                        available,
                        meetingDate
                );

        return studyGroupRepository.findAll(specification, pageable)
                .map(studyGroupMapper::toResponseDto);
    }

    // Modification d'un groupe
    @Transactional
    public StudyGroupResponseDTO updateStudyGroup(
            Long id,
            UpdateStudyGroupRequestDTO request
    ) {

        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);

        studyGroupMapper.updateEntity(studyGroup, request);

        studyGroup = studyGroupRepository.save(studyGroup);

        return studyGroupMapper.toResponseDto(studyGroup);
    }

    // Suppression logique d'un groupe
    @Transactional
    public void deleteStudyGroup(Long id) {

        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);

        studyGroup.setDeleted(true);
        studyGroupRepository.save(studyGroup);
    }

    // Récupère l'utilisateur actuellement authentifié
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

    // Recherche un groupe par son identifiant
    private StudyGroup findStudyGroupByIdOrThrow(Long id) {

        return studyGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new StudyGroupNotFoundException(
                                "Groupe introuvable avec l'id : " + id));
    }
}
