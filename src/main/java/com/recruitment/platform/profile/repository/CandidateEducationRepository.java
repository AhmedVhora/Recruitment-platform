package com.recruitment.platform.profile.repository;

import com.recruitment.platform.profile.entity.CandidateEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateEducationRepository
        extends JpaRepository<CandidateEducation, Long> {

    List<CandidateEducation> findByCandidateProfileId(Long candidateProfileId);
    Optional<CandidateEducation> findByIdAndCandidateProfileId(
            Long educationId,
            Long candidateProfileId
    );
}