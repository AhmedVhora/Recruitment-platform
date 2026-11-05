package com.recruitment.platform.profile.repository;

import com.recruitment.platform.profile.entity.CandidateCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateCertificationRepository
        extends JpaRepository<CandidateCertification, Long> {

    List<CandidateCertification> findByCandidateProfileId(Long candidateProfileId);
    boolean existsByCandidateProfileIdAndNameIgnoreCase(
            Long candidateProfileId,
            String name
    );

    Optional<CandidateCertification> findByIdAndCandidateProfileId(
            Long certificationId,
            Long candidateProfileId
    );

    boolean existsByCandidateProfileIdAndNameIgnoreCaseAndIdNot(
            Long candidateProfileId,
            String name,
            Long certificationId
    );
}