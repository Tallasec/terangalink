package com.terangalink.backend.repository;

import com.terangalink.backend.entity.HousingReservation;
import com.terangalink.backend.enums.HousingReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HousingReservationRepository extends JpaRepository<HousingReservation, Long> {

    boolean existsByHousingIdAndStatus(Long housingId, HousingReservationStatus status);

    Optional<HousingReservation> findByHousingIdAndTenantIdAndStatus(
            Long housingId,
            Long tenantId,
            HousingReservationStatus status);

    List<HousingReservation> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<HousingReservation> findByHousingIdOrderByCreatedAtDesc(Long housingId);
}
