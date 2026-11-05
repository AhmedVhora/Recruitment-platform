
package com.recruitment.platform.job.dto;

import com.recruitment.platform.job.entity.enums.EmploymentType;
import com.recruitment.platform.job.entity.enums.WorkMode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class JobSearchRequest {

    @Size(max = 100)
    private String keyword;

    @Size(max = 100)
    private String jobCategory;

    @Size(max = 255)
    private String location;

    private EmploymentType employmentType;

    private WorkMode workMode;

    @PositiveOrZero
    private Integer minExperience;

    @DecimalMin("0.0")
    private BigDecimal minSalary;

    @DecimalMin("0.0")
    private BigDecimal maxSalary;

    @Positive
    private Long companyId;

    @NotNull
    @PositiveOrZero
    private Integer page = 0;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer size = 10;

    @Pattern(regexp = "^(newest|salary_asc|salary_desc)$")
    private String sort = "newest";

    public JobSearchRequest() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
