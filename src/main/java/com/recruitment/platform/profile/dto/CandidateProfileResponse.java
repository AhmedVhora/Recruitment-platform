package com.recruitment.platform.profile.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CandidateProfileResponse {

    private Long id;

    private String headline;

    private String professionalSummary;

    private String location;

    private Integer yearsOfExperience;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    private Set<String> skills;

    private List<String> resumeUrls;

    private List<CandidateProjectResponse> projects;

    private List<CandidateCertificationResponse> certifications;

    private List<CandidateEducationResponse> education;

    private List<CandidateExperienceResponse> experiences;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CandidateProfileResponse() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public List<String> getResumeUrls() {
        return resumeUrls;
    }

    public void setResumeUrls(List<String> resumeUrls) {
        this.resumeUrls = resumeUrls;
    }

    public List<CandidateProjectResponse> getProjects() {
        return projects;
    }

    public void setProjects(List<CandidateProjectResponse> projects) {
        this.projects = projects;
    }

    public List<CandidateCertificationResponse> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CandidateCertificationResponse> certifications) {
        this.certifications = certifications;
    }

    public List<CandidateEducationResponse> getEducation() {
        return education;
    }

    public void setEducation(List<CandidateEducationResponse> education) {
        this.education = education;
    }

    public List<CandidateExperienceResponse> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<CandidateExperienceResponse> experiences) {
        this.experiences = experiences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}