package com.recruitment.platform.profile.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public class CreateCandidateProfileRequest {

    @Size(max = 255)
    private String headline;

    @Size(max = 2000)
    private String professionalSummary;

    @Size(max = 255)
    private String location;

    @Min(0)
    private Integer yearsOfExperience;

    @Size(max = 500)
    private String linkedinUrl;

    @Size(max = 500)
    private String githubUrl;

    @Size(max = 500)
    private String portfolioUrl;

    private Set<String> skills;

    private List<String> resumeUrls;

    public CreateCandidateProfileRequest() {
    }

    // Getters and Setters

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
}