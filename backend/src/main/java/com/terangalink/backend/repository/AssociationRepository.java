package com.terangalink.backend.repository;

import com.terangalink.backend.entity.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
ASSOCIATION REPOSITORY

Gere l'acces aux associations.
*/

@Repository
public interface AssociationRepository extends JpaRepository<Association, Long>, JpaSpecificationExecutor<Association> {

    Optional<Association> findByIdAndDeletedFalse(Long id);

    List<Association> findByCreatorIdAndDeletedFalseOrderByCreatedAtDesc(Long creatorId);
}
