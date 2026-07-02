package com.terangalink.backend.repository;

import com.terangalink.backend.entity.HousingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface HousingRepository extends
        JpaRepository<HousingPost, Long>,
        JpaSpecificationExecutor<HousingPost> {
}
