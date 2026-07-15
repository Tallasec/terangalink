package com.terangalink.backend.specification;

import com.terangalink.backend.entity.StudyGroup;
import com.terangalink.backend.enums.MeetingType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Locale;

/*
STUDY GROUP SPECIFICATION

Construit dynamiquement les filtres de recherche
des groupes de révision.
*/
public final class StudyGroupSpecification {

    // Empêche l'instanciation de la classe
    private StudyGroupSpecification() {
    }

    // Filtre sur le titre
    public static Specification<StudyGroup> hasTitle(String title) {

        return (root, query, criteriaBuilder) -> {

            if (title == null || title.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + title.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    // Filtre sur la matière
    public static Specification<StudyGroup> hasSubject(String subject) {

        return (root, query, criteriaBuilder) -> {

            if (subject == null || subject.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("subject")),
                    "%" + subject.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    // Filtre sur la ville
    public static Specification<StudyGroup> hasCity(String city) {

        return (root, query, criteriaBuilder) -> {

            if (city == null || city.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("city")),
                    "%" + city.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    // Filtre sur le type de rencontre
    public static Specification<StudyGroup> hasMeetingType(MeetingType meetingType) {

        return (root, query, criteriaBuilder) -> {

            if (meetingType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("meetingType"),
                    meetingType
            );
        };
    }

    // Filtre sur la disponibilité
    public static Specification<StudyGroup> isAvailable(Boolean available) {

        return (root, query, criteriaBuilder) -> {

            if (available == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("available"),
                    available
            );
        };
    }

    // Filtre sur la date de rencontre
    public static Specification<StudyGroup> hasMeetingDate(LocalDateTime meetingDate) {

        return (root, query, criteriaBuilder) -> {

            if (meetingDate == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("meetingDate"),
                    meetingDate
            );
        };
    }

    // Filtre sur les groupes non supprimés
    public static Specification<StudyGroup> isNotDeleted() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }

    // Construit la recherche dynamique
    public static Specification<StudyGroup> build(
            String title,
            String subject,
            String city,
            MeetingType meetingType,
            Boolean available,
            LocalDateTime meetingDate
    ) {

        return Specification
                .where(isNotDeleted())
                .and(hasTitle(title))
                .and(hasSubject(subject))
                .and(hasCity(city))
                .and(hasMeetingType(meetingType))
                .and(isAvailable(available))
                .and(hasMeetingDate(meetingDate));
    }
}
