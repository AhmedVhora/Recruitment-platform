package com.recruitment.platform.job.repository;

import com.recruitment.platform.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<Job,Long> , JpaSpecificationExecutor<Job> {
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);
}
