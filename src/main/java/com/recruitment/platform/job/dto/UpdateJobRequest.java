package com.recruitment.platform.job.dto;

import com.recruitment.platform.job.entity.enums.EmploymentType;
import com.recruitment.platform.job.entity.enums.WorkMode;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class UpdateJobRequest {

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String jobCategory;

    private String description;

    private String responsibilities;

    private Set<String> requiredSkills;

    private String qualifications;

    @PositiveOrZero
    private Integer minExperience;

    private EmploymentType employmentType;

    private WorkMode workMode;

    private String location;

    @PositiveOrZero
    private BigDecimal minSalary;

    @PositiveOrZero
    private BigDecimal maxSalary;

    @Positive
    private Integer totalVacancies;

    private LocalDate applicationDeadline;

    public UpdateJobRequest() {
    }

    public UpdateJobRequest(String title, String jobCategory,
                            String description, String responsibilities,
                            Set<String> requiredSkills, String qualifications,
                            Integer minExperience, EmploymentType employmentType,
                            WorkMode workMode, String location,
                            BigDecimal minSalary, BigDecimal maxSalary,
                            Integer totalVacancies, LocalDate applicationDeadline) {

        this.title = title;
        this.jobCategory = jobCategory;
        this.description = description;
        this.responsibilities = responsibilities;
        this.requiredSkills = requiredSkills;
        this.qualifications = qualifications;
        this.minExperience = minExperience;
        this.employmentType = employmentType;
        this.workMode = workMode;
        this.location = location;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.totalVacancies = totalVacancies;
        this.applicationDeadline = applicationDeadline;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Integer getTotalVacancies() {
        return totalVacancies;
    }

    public void setTotalVacancies(Integer totalVacancies) {
        this.totalVacancies = totalVacancies;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }
}