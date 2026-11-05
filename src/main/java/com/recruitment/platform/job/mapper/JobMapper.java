
package com.recruitment.platform.job.mapper;

import com.recruitment.platform.job.dto.CreateJobRequest;
import com.recruitment.platform.job.dto.JobResponse;
import com.recruitment.platform.job.entity.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public Job toEntity(CreateJobRequest request) {

        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setJobCategory(request.getJobCategory());
        job.setDescription(request.getDescription());
        job.setResponsibilities(request.getResponsibilities());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setQualifications(request.getQualifications());
        job.setMinExperience(request.getMinExperience());
        job.setEmploymentType(request.getEmploymentType());
        job.setWorkMode(request.getWorkMode());
        job.setLocation(request.getLocation());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setTotalVacancies(request.getTotalVacancies());
        job.setApplicationDeadline(request.getApplicationDeadline());

        return job;
    }

    public JobResponse toResponse(Job job) {

        JobResponse response = new JobResponse();

        response.setId(job.getId());
        response.setCompanyId(job.getCompany().getId());
        response.setTitle(job.getTitle());
        response.setJobCategory(job.getJobCategory());
        response.setDescription(job.getDescription());
        response.setResponsibilities(job.getResponsibilities());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setQualifications(job.getQualifications());
        response.setMinExperience(job.getMinExperience());
        response.setEmploymentType(job.getEmploymentType());
        response.setWorkMode(job.getWorkMode());
        response.setLocation(job.getLocation());
        response.setMinSalary(job.getMinSalary());
        response.setMaxSalary(job.getMaxSalary());
        response.setTotalVacancies(job.getTotalVacancies());
        response.setRemainingVacancies(job.getRemainingVacancies());
        response.setApplicationDeadline(job.getApplicationDeadline());
        response.setStatus(job.getStatus());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());

        return response;
    }
}
