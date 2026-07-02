package com.terangalink.backend.repository;

import com.terangalink.backend.entity.HousingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingImageRepository extends JpaRepository<HousingImage, Long> {

    long countByHousingId(Long housingId);

    List<HousingImage> findByHousingId(Long housingId);
}
