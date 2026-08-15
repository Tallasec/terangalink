package com.terangalink.backend.service;

import com.terangalink.backend.entity.StudyGroup;
import com.terangalink.backend.entity.StudyGroupMember;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.MeetingType;
import com.terangalink.backend.exception.business.InvalidCredentialsException;
import com.terangalink.backend.exception.business.StudyGroupClosedException;
import com.terangalink.backend.exception.business.StudyGroupFullException;
import com.terangalink.backend.exception.business.StudyGroupMembershipAlreadyExistsException;
import com.terangalink.backend.exception.business.StudyGroupMembershipNotFoundException;
import com.terangalink.backend.exception.business.StudyGroupNotFoundException;
import com.terangalink.backend.exception.business.UserNotFoundException;
import com.terangalink.backend.mapper.StudyGroupMapper;
import com.terangalink.backend.repository.StudyGroupMemberRepository;
import com.terangalink.backend.repository.StudyGroupRepository;
import com.terangalink.backend.repository.UserRepository;
import com.terangalink.backend.requestDTO.CreateStudyGroupRequestDTO;
import com.terangalink.backend.requestDTO.UpdateStudyGroupRequestDTO;
import com.terangalink.backend.responseDTO.StudyGroupMemberResponseDTO;
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
import java.util.List;

/*
STUDY GROUP SERVICE

Gère les opérations métier
liées aux groupes de révision.
*/

@Service
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final UserRepository userRepository;
    private final StudyGroupMapper studyGroupMapper;

    public StudyGroupService(
            StudyGroupRepository studyGroupRepository,
            StudyGroupMemberRepository studyGroupMemberRepository,
            UserRepository userRepository,
            StudyGroupMapper studyGroupMapper
    ) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroupMemberRepository = studyGroupMemberRepository;
        this.userRepository = userRepository;
        this.studyGroupMapper = studyGroupMapper;
    }

    @Transactional
    public StudyGroupResponseDTO createStudyGroup(CreateStudyGroupRequestDTO request) {
        UserPrincipal principal = getCurrentPrincipal();
        User creator = findUserByPrincipal(principal);

        StudyGroup studyGroup = studyGroupMapper.toEntity(request);
        studyGroup.setCreator(creator);
        studyGroup = studyGroupRepository.save(studyGroup);

        if (hasStudyGroupMembersRepository()) {
            addMemberToStudyGroup(studyGroup, creator);
        }

        return toResponseDto(studyGroup);
    }

    public StudyGroupResponseDTO getStudyGroupById(Long id) {
        return toResponseDto(findStudyGroupByIdOrThrow(id));
    }

    public Page<StudyGroupResponseDTO> getAllStudyGroups(Pageable pageable) {
        Specification<StudyGroup> specification = StudyGroupSpecification.build(
                null,
                null,
                null,
                null,
                null,
                null
        );

        return studyGroupRepository.findAll(specification, pageable)
                .map(this::toResponseDto);
    }

    public Page<StudyGroupResponseDTO> searchStudyGroups(
            String title,
            String subject,
            String city,
            MeetingType meetingType,
            Boolean available,
            LocalDateTime meetingDate,
            Pageable pageable
    ) {
        Specification<StudyGroup> specification = StudyGroupSpecification.build(
                title,
                subject,
                city,
                meetingType,
                available,
                meetingDate
        );

        return studyGroupRepository.findAll(specification, pageable)
                .map(this::toResponseDto);
    }

    @Transactional
    public StudyGroupResponseDTO updateStudyGroup(Long id, UpdateStudyGroupRequestDTO request) {
        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);

        studyGroupMapper.updateEntity(studyGroup, request);
        studyGroup = studyGroupRepository.save(studyGroup);

        return toResponseDto(studyGroup);
    }

    @Transactional
    public void deleteStudyGroup(Long id) {
        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);
        studyGroup.setDeleted(true);
        studyGroupRepository.save(studyGroup);
    }

    @Transactional
    public StudyGroupResponseDTO joinStudyGroup(Long id) {
        UserPrincipal principal = getCurrentPrincipal();
        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);
        User user = findUserByPrincipal(principal);

        ensureJoinable(studyGroup, user.getId());
        addMemberToStudyGroup(studyGroup, user);

        return toResponseDto(studyGroup);
    }

    @Transactional
    public StudyGroupResponseDTO leaveStudyGroup(Long id) {
        UserPrincipal principal = getCurrentPrincipal();
        StudyGroup studyGroup = findStudyGroupByIdOrThrow(id);
        User user = findUserByPrincipal(principal);

        if (studyGroup.getCreator() != null
                && studyGroup.getCreator().getId() != null
                && studyGroup.getCreator().getId().equals(user.getId())) {
            throw new StudyGroupMembershipNotFoundException(
                    "Le createur du groupe ne peut pas le quitter.");
        }

        StudyGroupMember membership = studyGroupMemberRepository
                .findByStudyGroupIdAndUserId(id, user.getId())
                .orElseThrow(() -> new StudyGroupMembershipNotFoundException(
                        "Vous n'etes pas membre de ce groupe."));

        studyGroupMemberRepository.delete(membership);

        return toResponseDto(studyGroup);
    }

    public List<StudyGroupMemberResponseDTO> getStudyGroupMembers(Long id) {
        findStudyGroupByIdOrThrow(id);

        if (!hasStudyGroupMembersRepository()) {
            return List.of();
        }

        return studyGroupMemberRepository.findByStudyGroupIdOrderByJoinedAtAsc(id)
                .stream()
                .map(this::toMemberResponseDto)
                .toList();
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

    private User findUserByPrincipal(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Utilisateur introuvable avec l'id : " + principal.getId()));
    }

    private StudyGroup findStudyGroupByIdOrThrow(Long id) {
        return studyGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new StudyGroupNotFoundException(
                        "Groupe introuvable avec l'id : " + id));
    }

    private void ensureJoinable(StudyGroup studyGroup, Long userId) {
        if (!hasStudyGroupMembersRepository()) {
            throw new StudyGroupClosedException("Les inscriptions ne sont pas disponibles.");
        }

        if (!studyGroup.isAvailable()) {
            throw new StudyGroupClosedException("Ce groupe n'accepte plus de nouveaux membres.");
        }

        if (studyGroupMemberRepository.existsByStudyGroupIdAndUserId(studyGroup.getId(), userId)) {
            throw new StudyGroupMembershipAlreadyExistsException("Vous etes deja membre de ce groupe.");
        }

        long memberCount = studyGroupMemberRepository.countByStudyGroupId(studyGroup.getId());
        if (memberCount >= studyGroup.getMaxMembers()) {
            throw new StudyGroupFullException("Ce groupe est complet.");
        }
    }

    private void addMemberToStudyGroup(StudyGroup studyGroup, User user) {
        if (!hasStudyGroupMembersRepository()) {
            return;
        }

        if (studyGroupMemberRepository.existsByStudyGroupIdAndUserId(studyGroup.getId(), user.getId())) {
            return;
        }

        StudyGroupMember member = new StudyGroupMember();
        member.setStudyGroup(studyGroup);
        member.setUser(user);
        studyGroupMemberRepository.save(member);
    }

    private StudyGroupResponseDTO toResponseDto(StudyGroup studyGroup) {
        StudyGroupResponseDTO response = studyGroupMapper.toResponseDto(studyGroup);
        long memberCount = hasStudyGroupMembersRepository()
                ? studyGroupMemberRepository.countByStudyGroupId(studyGroup.getId())
                : 0L;
        response.setMemberCount(memberCount);
        response.setFull(memberCount >= studyGroup.getMaxMembers());

        try {
            UserPrincipal principal = getCurrentPrincipal();
            response.setCurrentUserMember(
                    hasStudyGroupMembersRepository()
                            && studyGroupMemberRepository.existsByStudyGroupIdAndUserId(
                            studyGroup.getId(),
                            principal.getId()
                    )
            );
        } catch (InvalidCredentialsException ex) {
            response.setCurrentUserMember(false);
        }

        return response;
    }

    private boolean hasStudyGroupMembersRepository() {
        return studyGroupMemberRepository != null;
    }

    private StudyGroupMemberResponseDTO toMemberResponseDto(StudyGroupMember member) {
        StudyGroupMemberResponseDTO dto = new StudyGroupMemberResponseDTO();
        dto.setId(member.getId());

        if (member.getUser() != null) {
            dto.setUserId(member.getUser().getId());
            dto.setFirstName(member.getUser().getFirstName());
            dto.setLastName(member.getUser().getLastName());
            dto.setUniversity(member.getUser().getUniversity());
            dto.setFieldOfStudy(member.getUser().getFieldOfStudy());
            dto.setCity(member.getUser().getCity());
        }

        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }
}
