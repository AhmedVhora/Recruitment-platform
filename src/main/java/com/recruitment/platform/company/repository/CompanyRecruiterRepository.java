package com.recruitment.platform.company.repository;

import com.recruitment.platform.company.entity.CompanyRecruiter;
import com.recruitment.platform.company.entity.CompanyRecruiterId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRecruiterRepository extends JpaRepository<CompanyRecruiter, CompanyRecruiterId> {
    boolean existsByCompanyIdAndRecruiterId(Long companyId, Long recruiterId);
    List<CompanyRecruiter> findAllByRecruiterId(Long recruiterId);
    List<CompanyRecruiter> findAllByCompanyId(Long companyId);
    Optional<CompanyRecruiter> findByCompanyIdAndRecruiterId(
            Long companyId,
            Long recruiterId
    );
}

