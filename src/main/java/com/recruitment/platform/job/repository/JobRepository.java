package com.recruitment.platform.job.repository;

import com.recruitment.platform.job.entity.Job;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long> , JpaSpecificationExecutor<Job> {
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Job j WHERE j.id = :jobId")
    Optional<Job> findByIdForUpdate(@Param("jobId") Long jobId);
}
