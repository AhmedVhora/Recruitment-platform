package com.recruitment.platform.company.entity;

import com.recruitment.platform.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "company_recruiters")
public class CompanyRecruiter {

    @EmbeddedId
    private CompanyRecruiterId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("companyId")
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("recruiterId")
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;


    public CompanyRecruiter(Company company, User recruiter) {
        this.company = company;
        this.recruiter = recruiter;
        this.id = new CompanyRecruiterId(company.getId(), recruiter.getId());
    }

    public CompanyRecruiter() {
    }

    public CompanyRecruiterId getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public User getRecruiter() {
        return recruiter;
    }
}