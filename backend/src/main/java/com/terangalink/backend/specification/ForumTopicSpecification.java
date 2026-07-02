package com.terangalink.backend.specification;

import com.terangalink.backend.entity.ForumTopic;
import com.terangalink.backend.enums.ForumCategory;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

/*
FORUM SPECIFICATION

Construit dynamiquement les filtres
de recherche des sujets.
*/
public final class ForumTopicSpecification {

    // Empêche l'instanciation
    private ForumTopicSpecification() {
    }

    // Filtre sur le titre
    public static Specification<ForumTopic> hasTitle(String title) {

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

    // Filtre sur la catégorie
    public static Specification<ForumTopic> hasCategory(
            ForumCategory category
    ) {

        return (root, query, criteriaBuilder) -> {

            if (category == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("category"),
                    category
            );
        };
    }

    // Filtre sur les sujets non supprimés
    public static Specification<ForumTopic> isNotDeleted() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }

    // Construit la recherche dynamique
    public static Specification<ForumTopic> build(
            String title,
            ForumCategory category
    ) {

        return Specification
                .where(isNotDeleted())
                .and(hasTitle(title))
                .and(hasCategory(category));
    }

}