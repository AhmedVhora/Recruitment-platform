package com.recruitment.platform.profile.service;

import com.recruitment.platform.profile.dto.*;
import com.recruitment.platform.profile.entity.*;
import com.recruitment.platform.profile.mapper.CandidateProfileMapper;
import com.recruitment.platform.profile.repository.*;
import com.recruitment.platform.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.recruitment.platform.user.entity.UserRole.CANDIDATE;
import static com.recruitment.platform.user.entity.UserRole.RECRUITER;

@Service
public class CandidateProfileService {

    @Autowired
    private CandidateProfileRepository candidateProfileRepository;

    @Autowired
    private CandidateProfileMapper candidateProfileMapper;

    @Autowired
    private CandidateProjectRepository candidateProjectRepository;

    @Autowired
    private CandidateCertificationRepository candidateCertificationRepository;

    @Autowired
    private CandidateExperienceRepository candidateExperienceRepository;


    @Autowired
    private CandidateEducationRepository candidateEducationRepository;
    // Create Candidate Profile
    @Transactional
    public CandidateProfileResponse createProfile(
            User user,
            CreateCandidateProfileRequest request) {

        // 1. Verify that the user has the CANDIDATE role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can create a candidate profile."
            );
        }

        // 2. Check whether the candidate already has a profile
        if (candidateProfileRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Candidate profile already exists."
            );
        }

        // 3. Normalize skills
        Set<String> normalizedSkills = request.getSkills() == null
                ? new HashSet<>()
                : request.getSkills().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(skill -> skill.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        // 4. Convert request DTO into CandidateProfile entity
        CandidateProfile profile = candidateProfileMapper.toEntity(request);

        // 5. Associate the authenticated user with the profile
        profile.setUser(user);

        // 6. Assign normalized skills
        profile.setSkills(normalizedSkills);

        // 7. Save the candidate profile
        CandidateProfile savedProfile = candidateProfileRepository.save(profile);

        // 8. Convert saved entity into response DTO
        return candidateProfileMapper.toResponse(savedProfile);
    }

    // Get Candidate Profile
    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfile(User user) {

        // 1. Verify that the user has the CANDIDATE role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can access candidate profiles."
            );
        }

        // 2. Retrieve candidate profile
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Convert entity into response DTO
        return candidateProfileMapper.toResponse(profile);
    }


    // Update Candidate Profile
    @Transactional
    public CandidateProfileResponse updateProfile(
            User user,
            UpdateCandidateProfileRequest request) {

        // 1. Verify that the user has the CANDIDATE role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can update candidate profiles."
            );
        }

        // 2. Retrieve the candidate's existing profile
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Update basic profile fields if provided
        if (request.getHeadline() != null) {
            profile.setHeadline(request.getHeadline());
        }

        if (request.getProfessionalSummary() != null) {
            profile.setProfessionalSummary(request.getProfessionalSummary());
        }

        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }

        if (request.getYearsOfExperience() != null) {
            profile.setYearsOfExperience(request.getYearsOfExperience());
        }

        if (request.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(request.getLinkedinUrl());
        }

        if (request.getGithubUrl() != null) {
            profile.setGithubUrl(request.getGithubUrl());
        }

        if (request.getPortfolioUrl() != null) {
            profile.setPortfolioUrl(request.getPortfolioUrl());
        }

        // 4. Update skills if provided
        if (request.getSkills() != null) {

            Set<String> normalizedSkills = request.getSkills().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(skill -> !skill.isEmpty())
                    .map(skill -> skill.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());

            profile.getSkills().clear();
            profile.getSkills().addAll(normalizedSkills);
        }

        // 5. Update resume URLs if provided
        if (request.getResumeUrls() != null) {
            profile.getResumeUrls().clear();
            profile.getResumeUrls().addAll(request.getResumeUrls());
        }

        // 6. Save the updated profile
        CandidateProfile updatedProfile = candidateProfileRepository.save(profile);

        // 7. Convert updated entity into response DTO
        return candidateProfileMapper.toResponse(updatedProfile);
    }

    // Add Skills
    @Transactional
    public CandidateProfileResponse addSkills(
            User user,
            AddSkillsRequest request) {

        // 1. Verify that the user has the CANDIDATE role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can add skills."
            );
        }

        // 2. Retrieve the candidate's existing profile
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Normalize the incoming skills
        Set<String> normalizedSkills = request.getSkills().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(skill -> skill.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        // 4. Add skills without replacing existing skills
        profile.getSkills().addAll(normalizedSkills);

        // 5. Save the updated profile
        CandidateProfile updatedProfile = candidateProfileRepository.save(profile);

        // 6. Convert updated entity into response DTO
        return candidateProfileMapper.toResponse(updatedProfile);
    }


    // Remove Skill
    @Transactional
    public void removeSkill(User user, String skill) {

        // 1. Verify that the user has the CANDIDATE role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can remove skills."
            );
        }

        // 2. Retrieve the candidate's existing profile
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Normalize the skill
        String normalizedSkill = skill.trim().toLowerCase(Locale.ROOT);

        // 4. Check whether the skill exists
        if (!profile.getSkills().contains(normalizedSkill)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Skill not found in candidate profile."
            );
        }

        // 5. Remove the skill
        profile.getSkills().remove(normalizedSkill);

        // 6. Save the updated profile
        candidateProfileRepository.save(profile);
    }

    @Transactional
    public CandidateProfileResponse addProject(
            User user, AddCandidateProjectRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can add projects."
            );
        }

        // 2. Check whether candidate profile exists
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check for duplicate project
        String projectName = request.getProjectName().trim();

        if (candidateProjectRepository.existsByCandidateProfileIdAndProjectNameIgnoreCase(
                profile.getId(), projectName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Project already exists in your profile."
            );
        }

        // 4. Convert request to entity
        CandidateProject project = candidateProfileMapper.toEntity(request);

        // 5. Associate project with candidate profile
        project.setCandidateProfile(profile);

        // 6. Save project
        CandidateProject savedProject = candidateProjectRepository.save(project);

        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 7. Return response
        return candidateProfileMapper.toResponse(profile);
    }


    @Transactional
    public CandidateProfileResponse updateProject(
            User user,
            Long projectId,
            UpdateCandidateProjectRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can update projects."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check project existence and ownership
        CandidateProject project = candidateProjectRepository
                .findByIdAndCandidateProfileId(projectId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found."
                ));

        // 4. Check duplicate project name
        if (request.getProjectName() != null) {

            String projectName = request.getProjectName().trim();

            if (candidateProjectRepository
                    .existsByCandidateProfileIdAndProjectNameIgnoreCaseAndIdNot(
                            profile.getId(), projectName, projectId)) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A project with this name already exists."
                );
            }

            project.setProjectName(projectName);
        }

        // 5. Update only provided fields
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription().trim());
        }

        if (request.getRole() != null) {
            project.setRole(request.getRole());
        }

        if (request.getProjectUrl() != null) {
            project.setProjectUrl(request.getProjectUrl());
        }

        if (request.getTechnologies() != null) {
            project.setTechnologies(request.getTechnologies());
        }

        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }

        // 6. Save and return
        CandidateProject updatedProject = candidateProjectRepository.save(project);

        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        return candidateProfileMapper.toResponse(profile);
    }

    @Transactional
    public void deleteProject(User user, Long projectId) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can delete projects."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check project existence and ownership
        CandidateProject project = candidateProjectRepository
                .findByIdAndCandidateProfileId(projectId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found."
                ));

        // 4. Delete project
        candidateProjectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public CandidateProfileResponse getCandidateProfile(User user, Long candidateId) {

        // 1. Check recruiter role
        if (user.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view candidate profiles."
            );
        }

        // 2. Check whether candidate profile exists
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Return complete candidate profile

        return candidateProfileMapper.toResponse(profile);
    }

    // Add Certification
    @Transactional
    public CandidateProfileResponse addCertification(
            User user,
            AddCandidateCertificationRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can add certifications."
            );
        }

        // 2. Check whether candidate profile exists
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check for duplicate certification
        String certificationName = request.getName().trim();

        if (candidateCertificationRepository
                .existsByCandidateProfileIdAndNameIgnoreCase(
                        profile.getId(), certificationName)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Certification already exists in your profile."
            );
        }

        // 4. Convert request to entity
        CandidateCertification certification =
                candidateProfileMapper.toCertificationEntity(request);

        // 5. Associate certification with candidate profile
        certification.setCandidateProfile(profile);

        // 6. Save certification
        candidateCertificationRepository.save(certification);

        // 7. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 8. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Update Certification
    @Transactional
    public CandidateProfileResponse updateCertification(
            User user,
            Long certificationId,
            UpdateCandidateCertificationRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can update certifications."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check certification existence and ownership
        CandidateCertification certification = candidateCertificationRepository
                .findByIdAndCandidateProfileId(certificationId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Certification not found."
                ));

        // 4. Update certification name if provided
        if (request.getName() != null) {

            String certificationName = request.getName().trim();

            if (certificationName.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Certification name cannot be blank."
                );
            }

            if (candidateCertificationRepository
                    .existsByCandidateProfileIdAndNameIgnoreCaseAndIdNot(
                            profile.getId(), certificationName, certificationId)) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A certification with this name already exists."
                );
            }

            certification.setName(certificationName);
        }

        // 5. Update only provided fields
        if (request.getIssuingOrganization() != null) {
            String organization = request.getIssuingOrganization().trim();

            if (organization.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Issuing organization cannot be blank."
                );
            }

            certification.setIssuingOrganization(organization);
        }

        if (request.getIssueDate() != null) {
            certification.setIssueDate(request.getIssueDate());
        }

        if (request.getExpirationDate() != null) {
            certification.setExpirationDate(request.getExpirationDate());
        }

        if (request.getCredentialId() != null) {
            certification.setCredentialId(request.getCredentialId());
        }

        if (request.getCredentialUrl() != null) {
            certification.setCredentialUrl(request.getCredentialUrl());
        }

        // 6. Save updated certification
        candidateCertificationRepository.save(certification);

        // 7. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 8. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }


    // Delete Certification
    @Transactional
    public CandidateProfileResponse deleteCertification(
            User user, Long certificationId) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can delete certifications."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check certification existence and ownership
        CandidateCertification certification = candidateCertificationRepository
                .findByIdAndCandidateProfileId(certificationId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Certification not found."
                ));

        // 4. Delete certification
        candidateCertificationRepository.delete(certification);

        // 5. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 6. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Add Education
    @Transactional
    public CandidateProfileResponse addEducation(
            User user, AddCandidateEducationRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can add education."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Convert request to entity
        CandidateEducation education =
                candidateProfileMapper.toEducationEntity(request);

        // 4. Associate education with candidate profile
        education.setCandidateProfile(profile);

        // 5. Save education
        candidateEducationRepository.save(education);

        // 6. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 7. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Update Education
    @Transactional
    public CandidateProfileResponse updateEducation(
            User user,
            Long educationId,
            UpdateCandidateEducationRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can update education."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check education existence and ownership
        CandidateEducation education = candidateEducationRepository
                .findByIdAndCandidateProfileId(educationId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Education record not found."
                ));

        // 4. Update institution if provided
        if (request.getInstitution() != null) {
            String institution = request.getInstitution().trim();

            if (institution.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Institution cannot be blank."
                );
            }

            education.setInstitution(institution);
        }

        // 5. Update only provided fields
        if (request.getDegree() != null) {
            education.setDegree(request.getDegree());
        }

        if (request.getFieldOfStudy() != null) {
            education.setFieldOfStudy(request.getFieldOfStudy());
        }

        if (request.getStartDate() != null) {
            education.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            education.setEndDate(request.getEndDate());
        }

        if (request.getGrade() != null) {
            education.setGrade(request.getGrade());
        }

        // 6. Save updated education
        candidateEducationRepository.save(education);

        // 7. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 8. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Delete Education
    @Transactional
    public CandidateProfileResponse deleteEducation(User user, Long educationId) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can delete education."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check education existence and ownership
        CandidateEducation education = candidateEducationRepository
                .findByIdAndCandidateProfileId(educationId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Education record not found."
                ));

        // 4. Delete education
        candidateEducationRepository.delete(education);

        // 5. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 6. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Add Work Experience
    @Transactional
    public CandidateProfileResponse addExperience(
            User user,
            AddCandidateExperienceRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can add work experience."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Validate currentlyWorking and endDate
        if (Boolean.TRUE.equals(request.getCurrentlyWorking())
                && request.getEndDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be null for current employment."
            );
        }

        if (Boolean.FALSE.equals(request.getCurrentlyWorking())
                && request.getEndDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date is required for past employment."
            );
        }

        // 4. Convert request to entity
        CandidateExperience experience =
                candidateProfileMapper.toExperienceEntity(request);

        // 5. Associate experience with candidate profile
        experience.setCandidateProfile(profile);

        // 6. Save experience
        candidateExperienceRepository.save(experience);

        // 7. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 8. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Update Work Experience
    @Transactional
    public CandidateProfileResponse updateExperience(
            User user,
            Long experienceId,
            UpdateCandidateExperienceRequest request) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can update work experience."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check experience existence and ownership
        CandidateExperience experience = candidateExperienceRepository
                .findByIdAndCandidateProfileId(experienceId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Work experience record not found."
                ));

        // 4. Update provided fields
        if (request.getOrganizationName() != null) {
            String organizationName = request.getOrganizationName().trim();

            if (organizationName.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Organization name cannot be blank."
                );
            }

            experience.setOrganizationName(organizationName);
        }

        if (request.getJobTitle() != null) {
            String jobTitle = request.getJobTitle().trim();

            if (jobTitle.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Job title cannot be blank."
                );
            }

            experience.setJobTitle(jobTitle);
        }

        if (request.getLocation() != null) {
            experience.setLocation(request.getLocation());
        }

        if (request.getStartDate() != null) {
            experience.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            experience.setEndDate(request.getEndDate());
        }

        if (request.getCurrentlyWorking() != null) {
            experience.setCurrentlyWorking(request.getCurrentlyWorking());
        }

        if (request.getDescription() != null) {
            experience.setDescription(request.getDescription());
        }

        // 5. Validate final employment status
        if (Boolean.TRUE.equals(experience.isCurrentlyWorking())
                && experience.getEndDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be null for current employment."
            );
        }

        if (Boolean.FALSE.equals(experience.isCurrentlyWorking())
                && experience.getEndDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date is required for past employment."
            );
        }

        // 6. Save updated experience
        candidateExperienceRepository.save(experience);

        // 7. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 8. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Delete Work Experience
    @Transactional
    public CandidateProfileResponse deleteExperience(
            User user,
            Long experienceId) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can delete work experience."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Check experience existence and ownership
        CandidateExperience experience = candidateExperienceRepository
                .findByIdAndCandidateProfileId(experienceId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Work experience record not found."
                ));

        // 4. Delete experience
        candidateExperienceRepository.delete(experience);

        // 5. Retrieve updated profile
        profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 6. Return complete profile
        return candidateProfileMapper.toResponse(profile);
    }

    // Delete Candidate Profile
    @Transactional
    public void deleteProfile(User user) {

        // 1. Check candidate role
        if (user.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can delete their profile."
            );
        }

        // 2. Check candidate profile existence
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate profile not found."
                ));

        // 3. Delete candidate profile
        candidateProfileRepository.delete(profile);
    }
}