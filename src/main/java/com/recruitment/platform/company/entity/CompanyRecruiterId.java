package com.recruitment.platform.company.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CompanyRecruiterId implements Serializable {

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "recruiter_id")
    private long recruiterId;

    public CompanyRecruiterId() {
    }

    public CompanyRecruiterId(long companyId, long recruiterId) {
        this.companyId = companyId;
        this.recruiterId = recruiterId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public long getRecruiterId() {
        return recruiterId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CompanyRecruiterId)) {
            return false;
        }

        CompanyRecruiterId other = (CompanyRecruiterId) obj;

        return companyId == other.companyId
                && recruiterId == other.recruiterId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, recruiterId);
    }
}
