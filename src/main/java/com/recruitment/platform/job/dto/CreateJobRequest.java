
package com.recruitment.platform.job.dto;

import com.recruitment.platform.job.entity.enums.EmploymentType;
import com.recruitment.platform.job.entity.enums.WorkMode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class CreateJobRequest {

    @NotNull
    @Positive
    private Long companyId;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String jobCategory;

    @NotBlank
    private String description;

    private String responsibilities;

    @NotEmpty
    private Set<String> requiredSkills;

    private String qualifications;

    @NotNull
    @PositiveOrZero
    private Integer minExperience;

    @NotNull
    private EmploymentType employmentType;

    @NotNull
    private WorkMode workMode;

    private String location;

    @PositiveOrZero
    private BigDecimal minSalary;

    @PositiveOrZero
    private BigDecimal maxSalary;

    @NotNull
    @Positive
    private Integer totalVacancies;

    @NotNull
    @FutureOrPresent
    private LocalDate applicationDeadline;

    public CreateJobRequest() {
    }

    public CreateJobRequest(Long companyId, String title, String jobCategory,
                            String description, String responsibilities,
                            Set<String> requiredSkills, String qualifications,
                            Integer minExperience, EmploymentType employmentType,
                            WorkMode workMode, String location,
                            BigDecimal minSalary, BigDecimal maxSalary,
                            Integer totalVacancies, LocalDate applicationDeadline) {
        this.companyId = companyId;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

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
