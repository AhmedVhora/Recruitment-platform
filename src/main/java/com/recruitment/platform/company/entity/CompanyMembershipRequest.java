package com.recruitment.platform.company.entity;

import com.recruitment.platform.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_membership_requests")
public class CompanyMembershipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "company_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Company company;

    @JoinColumn(name = "initiated_by", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private User initiatedBy;

    @JoinColumn(name = "decided_by", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private User decidedBy;

    @JoinColumn(name = "target_recruiter_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private User targetRecruiter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipRequestStatus requestStatus;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime decidedAt;

    @PrePersist
    protected void onCreate(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected  void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDecided(User decidedBy, MembershipRequestStatus status) {
        this.decidedBy = decidedBy;
        this.requestStatus = status;
        this.decidedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public User getInitiatedBy() {
        return initiatedBy;
    }

    public User getDecidedBy() {
        return decidedBy;
    }

    public User getTargetRecruiter() {
        return targetRecruiter;
    }

    public MembershipRequestType getRequestType() {
        return requestType;
    }

    public MembershipRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public CompanyMembershipRequest(
            Company company,
            User targetRecruiter,
            MembershipRequestType requestType,
            User initiatedBy) {

        this.company = company;
        this.targetRecruiter = targetRecruiter;
        this.requestType = requestType;
        this.initiatedBy = initiatedBy;
        this.requestStatus = MembershipRequestStatus.PENDING;
    }

    public CompanyMembershipRequest(){

    }

}
