package com.recruitment.platform.company.repository;

import com.recruitment.platform.company.entity.CompanyMembershipRequest;
import com.recruitment.platform.company.entity.MembershipRequestStatus;
import com.recruitment.platform.company.entity.MembershipRequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyMembershipRequestRepository extends JpaRepository<CompanyMembershipRequest,Long> {
    boolean existsByCompanyIdAndTargetRecruiterIdAndRequestTypeAndRequestStatus(
            Long companyId,
            Long recruiterId,
            MembershipRequestType requestType,
            MembershipRequestStatus requestStatus
    );

    Optional<CompanyMembershipRequest>
    findByCompanyIdAndTargetRecruiterIdAndRequestTypeAndRequestStatus(
            Long companyId,
            Long recruiterId,
            MembershipRequestType requestType,
            MembershipRequestStatus requestStatus
    );
    List<CompanyMembershipRequest> findAllByCompanyIdAndRequestTypeAndRequestStatus(
            Long companyId,
            MembershipRequestType requestType,
            MembershipRequestStatus requestStatus
    );

    List<CompanyMembershipRequest> findAllByCompanyIdAndRequestType(
            Long companyId,
            MembershipRequestType requestType
    );

    List<CompanyMembershipRequest> findAllByTargetRecruiterIdAndRequestType(
            Long recruiterId,
            MembershipRequestType requestType
    );
    List<CompanyMembershipRequest> findAllByTargetRecruiterIdAndRequestTypeAndRequestStatus(
            Long recruiterId,
            MembershipRequestType requestType,
            MembershipRequestStatus requestStatus
    );
}
