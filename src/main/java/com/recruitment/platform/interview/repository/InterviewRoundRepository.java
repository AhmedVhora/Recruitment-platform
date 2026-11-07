package com.recruitment.platform.interview.repository;

import com.recruitment.platform.interview.entity.InterviewRound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Long> {

    // Get all rounds belonging to a process, in round order
    Page<InterviewRound> findByInterviewProcessIdOrderByRoundNumberAsc(
            Long interviewProcessId, Pageable pageable);

    // Get the latest round in a process
    Optional<InterviewRound> findTopByInterviewProcessIdOrderByRoundNumberDesc(
            Long interviewProcessId);

    // Get a specific round within a process
    Optional<InterviewRound> findByInterviewProcessIdAndRoundNumber(
            Long interviewProcessId, Integer roundNumber);

    // Candidate: get their interviews
    Page<InterviewRound> findByInterviewProcess_Application_Candidate_Id(
            Long candidateId, Pageable pageable);

    // Recruiter: get interviews assigned to them
    Page<InterviewRound> findByInterviewerId(
            Long interviewerId, Pageable pageable);

    // Recruiter: get interviews belonging to a particular job
    Page<InterviewRound> findByInterviewProcess_Application_Job_Id(
            Long jobId, Pageable pageable);

}