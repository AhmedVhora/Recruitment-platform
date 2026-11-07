package com.recruitment.platform.company.repository;

import com.recruitment.platform.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existsByNormalizedName(String normalizedName);
    boolean existsByNormalizedNameAndIdNot(String normalizedName, Long id);
    Page<Company> findByNormalizedNameContaining(
            String normalizedName, Pageable pageable);
    List<Company> findAllByCreatedById(Long userId);
}
