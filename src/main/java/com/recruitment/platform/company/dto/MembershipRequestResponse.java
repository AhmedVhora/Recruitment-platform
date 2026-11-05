package com.recruitment.platform.company.dto;

import com.recruitment.platform.company.entity.MembershipRequestStatus;
import com.recruitment.platform.company.entity.MembershipRequestType;

public class MembershipRequestResponse {
    private Long id;
    private Long companyId;
    private String companyName	;
    private Long targetRecruiterId;
    private MembershipRequestType requestType;
    private MembershipRequestStatus requestStatus;
    private Long initiatedById;

    public MembershipRequestResponse() {
    }

    public MembershipRequestResponse(Long id, Long companyId, String companyName, Long targetRecruiterId, MembershipRequestType requestType, MembershipRequestStatus requestStatus, Long initiatedById) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.targetRecruiterId = targetRecruiterId;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.initiatedById = initiatedById;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getTargetRecruiterId() {
        return targetRecruiterId;
    }

    public void setTargetRecruiterId(Long targetRecruiterId) {
        this.targetRecruiterId = targetRecruiterId;
    }

    public MembershipRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(MembershipRequestType requestType) {
        this.requestType = requestType;
    }

    public MembershipRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(MembershipRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Long getInitiatedById() {
        return initiatedById;
    }

    public void setInitiatedById(Long initiatedById) {
        this.initiatedById = initiatedById;
    }
}
