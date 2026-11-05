package com.recruitment.platform.profile.repository;

import com.recruitment.platform.profile.entity.CandidateProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateProjectRepository
        extends JpaRepository<CandidateProject, Long> {

    List<CandidateProject> findByCandidateProfileId(Long candidateProfileId);
    boolean existsByCandidateProfileIdAndProjectNameIgnoreCase(
            Long candidateProfileId,
            String projectName
    );
    Optional<CandidateProject> findByIdAndCandidateProfileId(
            Long projectId,
            Long candidateProfileId
    );
    boolean existsByCandidateProfileIdAndProjectNameIgnoreCaseAndIdNot(
            Long candidateProfileId,
            String projectName,
            Long projectId
    );
}