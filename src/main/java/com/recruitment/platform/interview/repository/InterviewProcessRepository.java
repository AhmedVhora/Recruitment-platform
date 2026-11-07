package com.recruitment.platform.interview.repository;

import com.recruitment.platform.interview.entity.InterviewProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewProcessRepository extends JpaRepository<InterviewProcess, Long> {

    Optional<InterviewProcess> findByApplicationId(Long applicationId);

}