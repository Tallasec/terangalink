package com.terangalink.backend.specification;

import com.terangalink.backend.entity.HousingPost;
import com.terangalink.backend.enums.HousingType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

/*
HOUSING SPECIFICATION

Construit dynamiquement les filtres de recherche
des logements.
*/
public final class HousingSpecification {

    // Empêche l'instanciation de la classe
    private HousingSpecification() {
    }

    // Filtre sur la ville
    public static Specification<HousingPost> hasCity(String city) {

        return (root, query, criteriaBuilder) -> {

            if (city == null || city.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("city")),
                    city.toLowerCase(Locale.ROOT)
            );
        };
    }

    // Filtre sur le type de logement
    public static Specification<HousingPost> hasHousingType(HousingType housingType) {

        return (root, query, criteriaBuilder) -> {

            if (housingType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("housingType"),
                    housingType
            );
        };
    }

    // Filtre sur la disponibilité
    public static Specification<HousingPost> isAvailable(Boolean available) {

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

    // Filtre sur le prix minimum
    public static Specification<HousingPost> hasMinPrice(BigDecimal minPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    // Filtre sur le prix maximum
    public static Specification<HousingPost> hasMaxPrice(BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }

    // Construit la recherche dynamique
    public static Specification<HousingPost> build(
            String city,
            HousingType housingType,
            Boolean available,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        return Specification
                .where(hasCity(city))
                .and(hasHousingType(housingType))
                .and(isAvailable(available))
                .and(hasMinPrice(minPrice))
                .and(hasMaxPrice(maxPrice));
    }
}