package com.recruitment.platform.profile.repository;

import com.recruitment.platform.profile.entity.CandidateExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateExperienceRepository
        extends JpaRepository<CandidateExperience, Long> {
    Optional<CandidateExperience> findByIdAndCandidateProfileId(
            Long experienceId,
            Long candidateProfileId
    );
    List<CandidateExperience> findByCandidateProfileId(Long candidateProfileId);
}