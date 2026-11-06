package com.recruitment.platform.application.repository;

import com.recruitment.platform.application.entity.Application;
import com.recruitment.platform.application.entity.enums.ApplicationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Check duplicate applications
    boolean existsByCandidate_IdAndJob_Id(Long candidateId, Long jobId);

    // Candidate's applications
    Page<Application> findByCandidate_Id(Long candidateId, Pageable pageable);

    Page<Application> findByCandidate_IdAndStatus(
            Long candidateId,
            ApplicationStatus status,
            Pageable pageable
    );

    // Retrieve an application belonging to a specific candidate
    Optional<Application> findByIdAndCandidate_Id(Long applicationId, Long candidateId);

    // Applications received for a particular job
    Page<Application> findByJob_Id(Long jobId, Pageable pageable);

    Page<Application> findByJob_IdAndStatus(
            Long jobId,
            ApplicationStatus status,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Application a WHERE a.id = :applicationId")
    Optional<Application> findByIdForUpdate(
            @Param("applicationId") Long applicationId
    );
}
