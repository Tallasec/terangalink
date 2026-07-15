package com.terangalink.backend.specification;

import com.terangalink.backend.entity.JobPost;
import com.terangalink.backend.enums.ContractType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

/*
JOB POST SPECIFICATION

Construit dynamiquement les filtres de recherche
des offres d'emploi.
*/
public final class JobPostSpecification {

    // Empêche l'instanciation de la classe
    private JobPostSpecification() {
    }

    // Filtre sur le titre
    public static Specification<JobPost> hasTitle(String title) {

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
    public static Specification<JobPost> hasCity(String city) {

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

    // Filtre sur le nom de l'entreprise
    public static Specification<JobPost> hasCompanyName(String companyName) {

        return (root, query, criteriaBuilder) -> {

            if (companyName == null || companyName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("companyName")),
                    "%" + companyName.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    // Filtre sur le type de contrat
    public static Specification<JobPost> hasContractType(ContractType contractType) {

        return (root, query, criteriaBuilder) -> {

            if (contractType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("contractType"),
                    contractType
            );
        };
    }

    // Filtre sur le salaire minimum
    public static Specification<JobPost> hasSalaryMin(BigDecimal salaryMin) {

        return (root, query, criteriaBuilder) -> {

            if (salaryMin == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("salary"),
                    salaryMin
            );
        };
    }

    // Filtre sur le salaire maximum
    public static Specification<JobPost> hasSalaryMax(BigDecimal salaryMax) {

        return (root, query, criteriaBuilder) -> {

            if (salaryMax == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("salary"),
                    salaryMax
            );
        };
    }

    // Filtre sur la disponibilité
    public static Specification<JobPost> isAvailable(Boolean available) {

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

    // Filtre sur les offres non supprimées
    public static Specification<JobPost> isNotDeleted() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }

    // Construit la recherche dynamique
    public static Specification<JobPost> build(
            String title,
            String city,
            String companyName,
            ContractType contractType,
            BigDecimal salaryMin,
            BigDecimal salaryMax,
            Boolean available
    ) {

        return Specification
                .where(isNotDeleted())
                .and(hasTitle(title))
                .and(hasCity(city))
                .and(hasCompanyName(companyName))
                .and(hasContractType(contractType))
                .and(hasSalaryMin(salaryMin))
                .and(hasSalaryMax(salaryMax))
                .and(isAvailable(available));
    }
}
