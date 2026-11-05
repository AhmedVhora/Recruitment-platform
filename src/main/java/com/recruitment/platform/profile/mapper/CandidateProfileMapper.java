package com.recruitment.platform.profile.mapper;

import com.recruitment.platform.profile.dto.*;
import com.recruitment.platform.profile.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CandidateProfileMapper {

    public CandidateProfile toEntity(CreateCandidateProfileRequest request) {

        CandidateProfile profile = new CandidateProfile();

        profile.setHeadline(request.getHeadline());
        profile.setProfessionalSummary(request.getProfessionalSummary());
        profile.setLocation(request.getLocation());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setSkills(request.getSkills());
        profile.setResumeUrls(request.getResumeUrls());

        return profile;
    }

    public CandidateProfileResponse toResponse(CandidateProfile profile) {

        CandidateProfileResponse response = new CandidateProfileResponse();

        // Basic profile information
        response.setId(profile.getId());
        response.setHeadline(profile.getHeadline());
        response.setProfessionalSummary(profile.getProfessionalSummary());
        response.setLocation(profile.getLocation());
        response.setYearsOfExperience(profile.getYearsOfExperience());
        response.setLinkedinUrl(profile.getLinkedinUrl());
        response.setGithubUrl(profile.getGithubUrl());
        response.setPortfolioUrl(profile.getPortfolioUrl());

        // Skills and resume URLs
        response.setSkills(profile.getSkills());
        response.setResumeUrls(profile.getResumeUrls());

        // Child entities
        response.setProjects(
                profile.getProjects().stream()
                        .map(this::toProjectResponse)
                        .collect(Collectors.toList())
        );

        response.setCertifications(
                profile.getCertifications().stream()
                        .map(this::toCertificationResponse)
                        .collect(Collectors.toList())
        );

        response.setEducation(
                profile.getEducation().stream()
                        .map(this::toEducationResponse)
                        .collect(Collectors.toList())
        );

        response.setExperiences(
                profile.getExperiences().stream()
                        .map(this::toExperienceResponse)
                        .collect(Collectors.toList())
        );

        // Timestamps
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        return response;
    }

    // Map CandidateProject to CandidateProjectResponse
    public CandidateProjectResponse toProjectResponse(CandidateProject project) {

        CandidateProjectResponse response = new CandidateProjectResponse();

        response.setId(project.getId());
        response.setProjectName(project.getProjectName());
        response.setDescription(project.getDescription());
        response.setRole(project.getRole());
        response.setProjectUrl(project.getProjectUrl());
        response.setTechnologies(project.getTechnologies());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());

        return response;
    }

    // Map CandidateCertification to CandidateCertificationResponse
    public CandidateCertificationResponse toCertificationResponse(
            CandidateCertification certification) {

        com.recruitment.platform.profile.dto.CandidateCertificationResponse response = new com.recruitment.platform.profile.dto.CandidateCertificationResponse();

        response.setId(certification.getId());
        response.setName(certification.getName());
        response.setIssuingOrganization(certification.getIssuingOrganization());
        response.setIssueDate(certification.getIssueDate());
        response.setExpirationDate(certification.getExpirationDate());
        response.setCredentialId(certification.getCredentialId());
        response.setCredentialUrl(certification.getCredentialUrl());

        return response;
    }

    // Map CandidateEducation to CandidateEducationResponse
    public CandidateEducationResponse toEducationResponse(CandidateEducation education) {

        CandidateEducationResponse response = new CandidateEducationResponse();

        response.setId(education.getId());
        response.setInstitution(education.getInstitution());
        response.setDegree(education.getDegree());
        response.setFieldOfStudy(education.getFieldOfStudy());
        response.setStartDate(education.getStartDate());
        response.setEndDate(education.getEndDate());
        response.setGrade(education.getGrade());

        return response;
    }

    // Map CandidateExperience to CandidateExperienceResponse
    public CandidateExperienceResponse toExperienceResponse(CandidateExperience experience) {

        com.recruitment.platform.profile.dto.CandidateExperienceResponse response = new com.recruitment.platform.profile.dto.CandidateExperienceResponse();

        response.setId(experience.getId());
        response.setOrganizationName(experience.getOrganizationName());
        response.setJobTitle(experience.getJobTitle());
        response.setLocation(experience.getLocation());
        response.setStartDate(experience.getStartDate());
        response.setEndDate(experience.getEndDate());
        response.setCurrentlyWorking(experience.isCurrentlyWorking());
        response.setDescription(experience.getDescription());

        return response;
    }

    public CandidateProject toEntity(AddCandidateProjectRequest request) {
        CandidateProject project = new CandidateProject();

        project.setProjectName(request.getProjectName().trim());
        project.setDescription(request.getDescription().trim());
        project.setRole(request.getRole());
        project.setProjectUrl(request.getProjectUrl());
        project.setTechnologies(request.getTechnologies());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        return project;
    }

    public CandidateCertification toCertificationEntity(
            AddCandidateCertificationRequest request) {

        CandidateCertification certification = new CandidateCertification();

        certification.setName(request.getName().trim());
        certification.setIssuingOrganization(request.getIssuingOrganization().trim());
        certification.setIssueDate(request.getIssueDate());
        certification.setExpirationDate(request.getExpirationDate());
        certification.setCredentialId(request.getCredentialId());
        certification.setCredentialUrl(request.getCredentialUrl());

        return certification;
    }

    public CandidateEducation toEducationEntity(
            AddCandidateEducationRequest request) {

        CandidateEducation education = new CandidateEducation();

        education.setInstitution(request.getInstitution().trim());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setGrade(request.getGrade());

        return education;
    }


    public CandidateExperience toExperienceEntity(
            AddCandidateExperienceRequest request) {

        CandidateExperience experience = new CandidateExperience();

        experience.setOrganizationName(request.getOrganizationName().trim());
        experience.setJobTitle(request.getJobTitle().trim());
        experience.setLocation(request.getLocation());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setCurrentlyWorking(request.getCurrentlyWorking());
        experience.setDescription(request.getDescription());

        return experience;
    }
}