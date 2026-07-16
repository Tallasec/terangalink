package com.terangalink.backend.specification;

import com.terangalink.backend.entity.Association;
import com.terangalink.backend.enums.AssociationType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

/*
ASSOCIATION SPECIFICATION

Construit dynamiquement les filtres de recherche
des associations.
*/
public final class AssociationSpecification {

    // Empche l'instanciation de la classe
    private AssociationSpecification() {
    }

    // Filtre sur le titre
    public static Specification<Association> hasTitle(String title) {

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

    // Filtre sur la ville
    public static Specification<Association> hasCity(String city) {

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

    // Filtre sur le type d'association
    public static Specification<Association> hasAssociationType(AssociationType associationType) {

        return (root, query, criteriaBuilder) -> {

            if (associationType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("associationType"),
                    associationType
            );
        };
    }

    // Filtre sur la disponibilite
    public static Specification<Association> isAvailable(Boolean available) {

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

    // Filtre sur les associations non supprimees
    public static Specification<Association> isNotDeleted() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }

    // Construit la recherche dynamique
    public static Specification<Association> build(
            String title,
            String city,
            AssociationType associationType,
            Boolean available
    ) {

        return Specification
                .where(isNotDeleted())
                .and(hasTitle(title))
                .and(hasCity(city))
                .and(hasAssociationType(associationType))
                .and(isAvailable(available));
    }
}
