package com.terangalink.backend.entity;

import com.terangalink.backend.enums.HousingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une annonce de logement publiée par un utilisateur.
 */
@Entity
@Table(name = "housing_posts")
@Getter
@Setter
@NoArgsConstructor
public class HousingPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre de l'annonce.
     */
    @NotBlank
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Description détaillée du logement.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Ville où se situe le logement.
     */
    @NotBlank
    @Column(nullable = false, length = 100)
    private String city;

    /**
     * Adresse du logement.
     */
    @Column(length = 255)
    private String address;

    /**
     * Prix mensuel du logement.
     */
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Type de logement.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HousingType housingType;

    /**
     * Disponibilité du logement.
     */
    @Column(nullable = false)
    private boolean available = true;

    /**
     * Propriétaire de l'annonce.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Date de création.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Date de dernière modification.
     */
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "housing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousingImage> images = new ArrayList<>();
}
