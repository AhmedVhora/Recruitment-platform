package com.recruitment.platform.company.dto;

import java.time.LocalDateTime;

public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private String industry;
    private String website;
    private String location;
    private String logoUrl;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIndustry() {
        return industry;
    }

    public String getWebsite() {
        return website;
    }

    public String getLocation() {
        return location;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CompanyResponse() {
    }

    public CompanyResponse(Long id, String name, String description, String industry, String website, String location, String logoUrl, Long createdById, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.website = website;
        this.location = location;
        this.logoUrl = logoUrl;
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
