package com.recruitment.platform.company.mapper;

import com.recruitment.platform.company.dto.CompanyResponse;
import com.recruitment.platform.company.dto.CreateCompanyRequest;
import com.recruitment.platform.company.dto.MembershipRequestResponse;
import com.recruitment.platform.company.entity.Company;
import com.recruitment.platform.company.entity.CompanyMembershipRequest;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    public Company toEntity(CreateCompanyRequest request){
        Company company = new Company();

        company.setDescription(request.getDescription());
        company.setIndustry(request.getIndustry());
        company.setLocation(request.getLocation());
        company.setName(request.getName().trim());
        company.setWebsite(request.getWebsite());
        company.setLogoUrl(request.getLogoUrl());
        return company;
    }

    public CompanyResponse toResponse(Company company){
        CompanyResponse response = new CompanyResponse(company.getId(),company.getName(), company.getDescription(), company.getIndustry(), company.getWebsite(), company.getLocation(), company.getLogoUrl(),company.getCreatedBy().getId(),company.getCreatedAt(),company.getUpdatedAt());
        return response;

    }

    public MembershipRequestResponse toMembershipRequestResponse(
            CompanyMembershipRequest request) {

        return new MembershipRequestResponse(
                request.getId(),
                request.getCompany().getId(),
                request.getCompany().getName(),
                request.getTargetRecruiter().getId(),
                request.getRequestType(),
                request.getRequestStatus(),
                request.getInitiatedBy().getId()
        );
    }



}
