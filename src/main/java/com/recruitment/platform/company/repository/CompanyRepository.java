package com.recruitment.platform.company.repository;

import com.recruitment.platform.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existsByNormalizedName(String normalizedName);
    boolean existsByNormalizedNameAndIdNot(String normalizedName, Long id);
    List<Company> findByNormalizedNameContaining(String normalizedName);
    List<Company> findAllByCreatedById(Long userId);
}
