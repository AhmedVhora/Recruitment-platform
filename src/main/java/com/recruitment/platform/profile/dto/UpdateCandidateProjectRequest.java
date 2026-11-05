package com.recruitment.platform.profile.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public class UpdateCandidateProjectRequest {


    @Size(max = 255)
    private String projectName;


    @Size(max = 2000)
    private String description;

    @Size(max = 255)
    private String role;

    @Size(max = 500)
    private String projectUrl;

    private Set<String> technologies;

    private LocalDate startDate;

    private LocalDate endDate;

    public UpdateCandidateProjectRequest() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public Set<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<String> technologies) {
        this.technologies = technologies;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}