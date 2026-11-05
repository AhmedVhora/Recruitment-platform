package com.recruitment.platform.profile.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateCandidateCertificationRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String issuingOrganization;

    private LocalDate issueDate;

    private LocalDate expirationDate;

    @Size(max = 255)
    private String credentialId;

    @Size(max = 500)
    private String credentialUrl;

    public UpdateCandidateCertificationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }
}