
package com.recruitment.platform.job.service;

import com.recruitment.platform.company.entity.Company;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.company.repository.CompanyRepository;
import com.recruitment.platform.job.dto.CreateJobRequest;
import com.recruitment.platform.job.dto.JobResponse;
import com.recruitment.platform.job.dto.JobSearchRequest;
import com.recruitment.platform.job.dto.UpdateJobRequest;
import com.recruitment.platform.job.entity.Job;
import com.recruitment.platform.job.entity.enums.JobStatus;
import com.recruitment.platform.job.entity.enums.WorkMode;
import com.recruitment.platform.job.mapper.JobMapper;
import com.recruitment.platform.job.repository.JobRepository;
import com.recruitment.platform.job.specification.JobSpecification;
import com.recruitment.platform.profile.dto.CandidateProfileResponse;
import com.recruitment.platform.profile.entity.CandidateProfile;
import com.recruitment.platform.profile.mapper.CandidateProfileMapper;
import com.recruitment.platform.profile.repository.CandidateProfileRepository;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.recruitment.platform.user.entity.UserRole.RECRUITER;

@Service
public class JobService {

    @Autowired
    private CompanyRecruiterRepository companyRecruiterRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    CandidateProfileRepository candidateProfileRepository;

    @Autowired
    CandidateProfileMapper candidateProfileMapper;

    @Transactional
    public JobResponse createJob(User user, CreateJobRequest request) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can create jobs."
            );
        }

        // 2. Find company
        Long companyId = request.getCompanyId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist."
                ));

        // 3. Verify active company membership
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                company.getId(), user.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters associated with the company can create jobs."
            );
        }

        // 4. Validate salary range
        if (request.getMinSalary() != null
                && request.getMaxSalary() != null
                && request.getMaxSalary().compareTo(request.getMinSalary()) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum salary cannot be less than minimum salary."
            );
        }

        // 5. Validate location
        if ((request.getWorkMode() == WorkMode.ONSITE
                || request.getWorkMode() == WorkMode.HYBRID)
                && (request.getLocation() == null
                || request.getLocation().isBlank())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Location is mandatory for onsite and hybrid jobs."
            );
        }

        // 6. Normalize required skills
        Set<String> normalizedSkills = request.getRequiredSkills()
                .stream()
                .filter(skill -> skill != null)
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(skill -> skill.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        if (normalizedSkills.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one valid required skill must be provided."
            );
        }

        // 7. Convert request DTO into entity
        Job job = jobMapper.toEntity(request);

        job.setRequiredSkills(normalizedSkills);
        job.setCompany(company);
        job.setCreatedBy(user);
        job.setLocation(request.getLocation() == null
                ? null : request.getLocation().trim());

        // 8. Initialize system-controlled fields
        job.setRemainingVacancies(request.getTotalVacancies());
        job.setStatus(JobStatus.DRAFT);

        // 9. Save job
        Job savedJob = jobRepository.save(job);

        // 10. Convert saved entity into response DTO
        return jobMapper.toResponse(savedJob);
    }

    @Transactional
    public JobResponse updateJob(Long jobId, User user, UpdateJobRequest request) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters can update jobs.");
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job does not exist."));

        // 3. Verify company membership
        Long companyId = job.getCompany().getId();

        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not associated with this company.");
        }

        // 4. Update supplied fields
        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Job title cannot be blank.");
            }
            job.setTitle(request.getTitle().trim());
        }

        if (request.getJobCategory() != null) {
            if (request.getJobCategory().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Job category cannot be blank.");
            }
            job.setJobCategory(request.getJobCategory().trim());
        }

        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Description cannot be blank.");
            }
            job.setDescription(request.getDescription());
        }

        if (request.getResponsibilities() != null) {
            job.setResponsibilities(request.getResponsibilities());
        }

        if (request.getRequiredSkills() != null) {
            Set<String> normalizedSkills = request.getRequiredSkills()
                    .stream()
                    .filter(skill -> skill != null)
                    .map(String::trim)
                    .filter(skill -> !skill.isEmpty())
                    .map(skill -> skill.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());

            if (normalizedSkills.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "At least one valid required skill must be provided.");
            }

            job.setRequiredSkills(normalizedSkills);
        }

        if (request.getQualifications() != null) {
            job.setQualifications(request.getQualifications());
        }

        if (request.getMinExperience() != null) {
            job.setMinExperience(request.getMinExperience());
        }

        if (request.getEmploymentType() != null) {
            job.setEmploymentType(request.getEmploymentType());
        }

        if (request.getWorkMode() != null) {
            job.setWorkMode(request.getWorkMode());
        }

        if (request.getLocation() != null) {
            job.setLocation(request.getLocation().trim());
        }

        if (request.getMinSalary() != null) {
            job.setMinSalary(request.getMinSalary());
        }

        if (request.getMaxSalary() != null) {
            job.setMaxSalary(request.getMaxSalary());
        }

        if (request.getApplicationDeadline() != null) {
            job.setApplicationDeadline(request.getApplicationDeadline());
        }

        // 5. Validate salary range
        if (job.getMinSalary() != null
                && job.getMaxSalary() != null
                && job.getMaxSalary().compareTo(job.getMinSalary()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Maximum salary cannot be less than minimum salary.");
        }

        // 6. Validate work mode and location
        if ((job.getWorkMode() == WorkMode.ONSITE
                || job.getWorkMode() == WorkMode.HYBRID)
                && (job.getLocation() == null || job.getLocation().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location is mandatory for onsite and hybrid jobs.");
        }

        // 7. Validate deadline
        if (job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Application deadline cannot be in the past.");
        }

        // 8. Update vacancies
        if (request.getTotalVacancies() != null) {

            int filledVacancies = job.getTotalVacancies()
                    - job.getRemainingVacancies();

            int newTotalVacancies = request.getTotalVacancies();

            if (newTotalVacancies < filledVacancies) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Total vacancies cannot be less than filled vacancies.");
            }

            job.setTotalVacancies(newTotalVacancies);
            job.setRemainingVacancies(newTotalVacancies - filledVacancies);

            if (job.getStatus() == JobStatus.CLOSED
                    && job.getRemainingVacancies() > 0
                    && job.getApplicationDeadline().isAfter(LocalDate.now())) {
                job.setStatus(JobStatus.PUBLISHED);
            }
        }

        // 9. Save and return
        Job updatedJob = jobRepository.save(job);

        return jobMapper.toResponse(updatedJob);
    }

    @Transactional
    public void deleteJob(Long jobId, User user) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters can delete jobs.");
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job does not exist."));

        // 3. Verify company membership
        Long companyId = job.getCompany().getId();

        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not associated with this company.");
        }

        // 4. Only draft jobs can be deleted
        if (job.getStatus() != JobStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only draft jobs can be deleted.");
        }

        // 5. Delete job
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long jobId, User user) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job does not exist."));

        // Recruiter: must belong to the job's company
        if (user.getRole() == RECRUITER) {

            if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                    job.getCompany().getId(), user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You are not associated with this company.");
            }

        }
        // Candidate: can view only available published jobs
        else if (user.getRole() == UserRole.CANDIDATE) {

            if (job.getStatus() != JobStatus.PUBLISHED
                    || job.getRemainingVacancies() <= 0
                    || job.getApplicationDeadline().isBefore(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Job is not available.");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not authorized to view this job.");
        }

        return jobMapper.toResponse(job);
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> getCompanyJobs(
            Long companyId, User user, Pageable pageable) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters can view company jobs.");
        }

        // 2. Verify company exists
        if (!companyRepository.existsById(companyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Company does not exist.");
        }

        // 3. Verify active company membership
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not associated with this company.");
        }

        // 4. Fetch and map paginated jobs
        return jobRepository.findByCompanyId(companyId, pageable)
                .map(jobMapper::toResponse);
    }

    @Transactional
    public JobResponse publishJob(Long jobId, User user) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters can publish jobs.");
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job does not exist."));

        // 3. Verify company membership
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                job.getCompany().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not associated with this company.");
        }

        // 4. Verify job status
        if (job.getStatus() != JobStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only draft jobs can be published.");
        }

        // 5. Verify application deadline
        if (job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot publish a job with an expired application deadline.");
        }

        // 6. Verify remaining vacancies
        if (job.getRemainingVacancies() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot publish a job with no remaining vacancies.");
        }

        // 7. Publish job
        job.setStatus(JobStatus.PUBLISHED);

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }

    @Transactional
    public JobResponse closeJob(Long jobId, User user) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters can close jobs.");
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job does not exist."));

        // 3. Verify company membership
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                job.getCompany().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not associated with this company.");
        }

        // 4. Only published jobs can be closed
        if (job.getStatus() != JobStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only published jobs can be closed.");
        }

        // 5. Close job
        job.setStatus(JobStatus.CLOSED);

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }

    @Transactional
    public JobResponse reopenJob(Long jobId, User user) {

        // 1. Verify recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters can reopen jobs.");
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job does not exist."));

        // 3. Verify company membership
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                job.getCompany().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not associated with this company.");
        }

        // 4. Only closed jobs can be reopened
        if (job.getStatus() != JobStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only closed jobs can be reopened.");
        }

        // 5. Verify application deadline
        if (job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot reopen a job with an expired application deadline.");
        }

        // 6. Verify remaining vacancies
        if (job.getRemainingVacancies() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot reopen a job with no remaining vacancies.");
        }

        // 7. Reopen job
        job.setStatus(JobStatus.PUBLISHED);

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }


    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(JobSearchRequest request) {

        // 1. Validate salary range
        if (request.getMinSalary() != null
                && request.getMaxSalary() != null
                && request.getMinSalary().compareTo(request.getMaxSalary()) > 0) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Minimum salary cannot exceed maximum salary.");
        }

        // 2. Determine sorting
        Sort sort;

        switch (request.getSort()) {

            case "salary_asc":
                sort = Sort.by(Sort.Direction.ASC, "minSalary");
                break;

            case "salary_desc":
                sort = Sort.by(Sort.Direction.DESC, "maxSalary");
                break;

            case "newest":
            default:
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        // 3. Create pagination
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        // 4. Build dynamic filtering specification
        Specification<Job> specification =
                JobSpecification.filterJobs(request);

        // 5. Execute query and map results
        return jobRepository.findAll(specification, pageable)
                .map(jobMapper::toResponse);
    }




}
