package com.recruitment.platform.company.service;

import com.recruitment.platform.company.dto.*;
import com.recruitment.platform.company.entity.*;
import com.recruitment.platform.company.mapper.CompanyMapper;
import com.recruitment.platform.company.repository.CompanyMembershipRequestRepository;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.company.repository.CompanyRepository;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.recruitment.platform.user.entity.UserRole.ADMIN;
import static com.recruitment.platform.user.entity.UserRole.RECRUITER;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyRecruiterRepository companyRecruiterRepository;

    @Autowired
    private CompanyMembershipRequestRepository companyMembershipRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyMapper companyMapper;
    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request, User recruiter){
        String companyName = request.getName();
        companyName=companyName.trim().toLowerCase(Locale.ROOT);

        if(!(recruiter.getRole() == RECRUITER || recruiter.getRole() == ADMIN) ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "A Candidate cannot create a company");
        }

        if(companyRepository.existsByNormalizedName(companyName)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A Company with this name already exists");
        }

        Company company = companyMapper.toEntity(request);
        company.setNormalizedName(companyName);
        company.setCreatedBy(recruiter);
        Company savedCompany = companyRepository.save(company);

        CompanyRecruiter companyRecruiter = new CompanyRecruiter(savedCompany,recruiter);
        companyRecruiterRepository.save(companyRecruiter);

        return companyMapper.toResponse(savedCompany);

    }
    @Transactional
    public CompanyResponse updateCompany(Long companyId,
                                         UpdateCompanyRequest request,
                                         User recruiter){
        Company company = companyRepository.findById(companyId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Company does not exist"));
        if(!(recruiter.getRole()== RECRUITER || recruiter.getRole()==ADMIN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Candidate cannot edit company details.");
        }
        if(company.getCreatedBy().getId() != (recruiter.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the owner can edit company details.");
        }

        if(request.getName() != null) {
            String name = request.getName().trim();
            if (name.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company name cannot consist of only whitespaces");
            }
            String normalizedName = name.toLowerCase(Locale.ROOT);
            if (companyRepository.existsByNormalizedNameAndIdNot(normalizedName, companyId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Company name already taken");
            }

            company.setName(name);
            company.setNormalizedName(normalizedName);
        }

        if(request.getDescription()!=null){
            company.setDescription(request.getDescription());
        }

        if(request.getIndustry()!=null){
            company.setIndustry(request.getIndustry());
        }

        if(request.getLocation()!=null){
            company.setLocation(request.getLocation());
        }

        if(request.getLogoUrl()!=null){
            company.setLogoUrl(request.getLogoUrl());
        }

        if(request.getWebsite()!=null){
            company.setWebsite(request.getWebsite());
        }

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponse(savedCompany);
    }

    public Page<CompanyResponse> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(companyMapper::toResponse);
    }

    public CompanyResponse getCompanyById(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        return companyMapper.toResponse(company);
    }

    public Page<CompanyResponse> searchCompanies(
            String name, Pageable pageable) {

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search term cannot be empty"
            );
        }

        String normalizedName = name.trim().toLowerCase(Locale.ROOT);

        return companyRepository
                .findByNormalizedNameContaining(normalizedName, pageable)
                .map(companyMapper::toResponse);
    }


    public List<CompanyResponse> getMyRecruiterCompanies(User recruiter) {
        if(!(recruiter.getRole()== RECRUITER || recruiter.getRole()==ADMIN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Candidate does not have companies");
        }
        List<CompanyRecruiter> memberships =
                companyRecruiterRepository.findAllByRecruiterId(recruiter.getId());

        return memberships.stream()
                .map(CompanyRecruiter::getCompany)
                .map(companyMapper::toResponse)
                .toList();
    }

    public List<CompanyResponse> getMyOwnedCompanies(User recruiter){
        if(!(recruiter.getRole()== RECRUITER || recruiter.getRole()==ADMIN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only recruiter or Admin can access this endpoint");
        }
        List<Company> memberships =
                companyRepository.findAllByCreatedById(recruiter.getId());

        return memberships.stream().map(companyMapper::toResponse).toList();

    }

    @Transactional
    public MembershipRequestResponse createJoinRequest(Long companyId, User recruiter) {

        // 1. Verify recruiter role
        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can request company membership"
            );
        }

        // 2. Retrieve company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        // 3. Check existing membership
        if (companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already a member of this company"
            );
        }

        // Check for an existing pending invitation
        Optional<CompanyMembershipRequest> invitation =
                companyMembershipRequestRepository
                        .findByCompanyIdAndTargetRecruiterIdAndRequestTypeAndRequestStatus(
                                companyId,
                                recruiter.getId(),
                                MembershipRequestType.INVITATION,
                                MembershipRequestStatus.PENDING
                        );

        if (invitation.isPresent()) {

            CompanyMembershipRequest existingInvitation = invitation.get();

            // Automatically accept the invitation
            existingInvitation.markAsDecided(
                    recruiter,
                    MembershipRequestStatus.APPROVED
            );

            companyMembershipRequestRepository.save(existingInvitation);

            // Add recruiter to the company
            CompanyRecruiter membership =
                    new CompanyRecruiter(company, recruiter);

            companyRecruiterRepository.save(membership);

            return companyMapper.toMembershipRequestResponse(existingInvitation);
        }

        // 4. Check duplicate pending request
        if (companyMembershipRequestRepository
                .existsByCompanyIdAndTargetRecruiterIdAndRequestTypeAndRequestStatus(
                        companyId,
                        recruiter.getId(),
                        MembershipRequestType.JOIN_REQUEST,
                        MembershipRequestStatus.PENDING)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have a pending join request for this company"
            );
        }

        // 5. Create join request
        CompanyMembershipRequest request = new CompanyMembershipRequest(
                company,
                recruiter,
                MembershipRequestType.JOIN_REQUEST,
                recruiter
        );

        // 6. Save request
        CompanyMembershipRequest savedRequest =
                companyMembershipRequestRepository.save(request);

        // 7. Return response
        return companyMapper.toMembershipRequestResponse(savedRequest);
    }


    public List<MembershipRequestResponse> getCompanyJoinRequests(
            Long companyId,
            User recruiter,
            MembershipRequestFilter filter) {

        // Only recruiters can access this endpoint
        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view company join requests"
            );
        }

        // Check whether the company exists
        companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        // Check whether the recruiter is an active member
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this company"
            );
        }

        List<CompanyMembershipRequest> requests;

        if (filter == MembershipRequestFilter.ALL) {

            requests = companyMembershipRequestRepository
                    .findAllByCompanyIdAndRequestType(
                            companyId,
                            MembershipRequestType.JOIN_REQUEST
                    );

        } else {

            requests = companyMembershipRequestRepository
                    .findAllByCompanyIdAndRequestTypeAndRequestStatus(
                            companyId,
                            MembershipRequestType.JOIN_REQUEST,
                            MembershipRequestStatus.valueOf(filter.name())
                    );
        }

        return requests.stream()
                .map(companyMapper::toMembershipRequestResponse)
                .toList();
    }

    @Transactional
    public MembershipRequestResponse decideJoinRequest(
            Long companyId,
            Long requestId,
            DecideMembershipRequest request,
            User recruiter) {

        // Only recruiters can decide join requests
        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can decide join requests"
            );
        }

        // Find the company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        // Check whether the recruiter is an active member
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this company"
            );
        }

        // Find the membership request
        CompanyMembershipRequest membershipRequest =
                companyMembershipRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Membership request does not exist"
                        ));

        // Verify that the request belongs to this company
        if (membershipRequest.getCompany().getId() != companyId) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This request does not belong to the specified company"
            );
        }

        // Verify that this is a join request
        if (membershipRequest.getRequestType() != MembershipRequestType.JOIN_REQUEST) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This is not a join request"
            );
        }

        // Verify that the request is still pending
        if (membershipRequest.getRequestStatus() != MembershipRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This request has already been decided"
            );
        }

        // Prevent the requester from deciding their own request
        if (membershipRequest.getTargetRecruiter().getId() == recruiter.getId()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot decide your own join request"
            );
        }

        MembershipRequestStatus status = request.getStatus();

        // Only APPROVED or REJECTED are valid decisions
        if (status != MembershipRequestStatus.APPROVED &&
                status != MembershipRequestStatus.REJECTED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid decision status"
            );
        }

        // Update the request status and decision details
        membershipRequest.markAsDecided(recruiter, status);

        companyMembershipRequestRepository.save(membershipRequest);

        // Create active membership only if the request is approved
        if (status == MembershipRequestStatus.APPROVED) {

            CompanyRecruiter membership = new CompanyRecruiter(
                    company,
                    membershipRequest.getTargetRecruiter()
            );

            companyRecruiterRepository.save(membership);
        }

        return companyMapper.toMembershipRequestResponse(membershipRequest);
    }



    @Transactional
    public MembershipRequestResponse sendInvitation(
            Long companyId,
            InviteRecruiterRequest request,
            User recruiter) {

        // 1. Verify sender role
        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can send company invitations"
            );
        }

        // 2. Find company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        // 3. Verify sender is an active company member
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this company"
            );
        }

        // 4. Find target recruiter
        User targetRecruiter = userRepository.findById(request.getRecruiterId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recruiter does not exist"
                ));

        // 5. Verify target user's role
        if (targetRecruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invitations can only be sent to recruiters"
            );
        }

        // 6. Prevent self-invitation
        if (targetRecruiter.getId() == recruiter.getId()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot invite yourself"
            );
        }

        // 7. Check existing membership
        if (companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, targetRecruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This recruiter is already a member of the company"
            );
        }

        // 8. Check for an existing pending join request
        Optional<CompanyMembershipRequest> joinRequest =
                companyMembershipRequestRepository
                        .findByCompanyIdAndTargetRecruiterIdAndRequestTypeAndRequestStatus(
                                companyId,
                                targetRecruiter.getId(),
                                MembershipRequestType.JOIN_REQUEST,
                                MembershipRequestStatus.PENDING
                        );

        if (joinRequest.isPresent()) {

            CompanyMembershipRequest existingRequest = joinRequest.get();

            // Automatically approve the existing join request
            existingRequest.markAsDecided(
                    recruiter,
                    MembershipRequestStatus.APPROVED
            );

            companyMembershipRequestRepository.save(existingRequest);

            // Create active membership
            CompanyRecruiter membership =
                    new CompanyRecruiter(company, targetRecruiter);

            companyRecruiterRepository.save(membership);

            return companyMapper.toMembershipRequestResponse(existingRequest);
        }

        // 9. Check for duplicate pending invitation
        if (companyMembershipRequestRepository
                .existsByCompanyIdAndTargetRecruiterIdAndRequestTypeAndRequestStatus(
                        companyId,
                        targetRecruiter.getId(),
                        MembershipRequestType.INVITATION,
                        MembershipRequestStatus.PENDING
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A pending invitation already exists for this recruiter"
            );
        }

        // 10. Create new invitation
        CompanyMembershipRequest invitation =
                new CompanyMembershipRequest(
                        company,
                        targetRecruiter,
                        MembershipRequestType.INVITATION,
                        recruiter
                );

        CompanyMembershipRequest savedInvitation =
                companyMembershipRequestRepository.save(invitation);

        return companyMapper.toMembershipRequestResponse(savedInvitation);
    }

    public List<MembershipRequestResponse> getCompanyInvitations(
            Long companyId,
            User recruiter,
            MembershipRequestFilter filter) {

        // Only recruiters can access this endpoint
        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view company invitations"
            );
        }

        // Check whether the company exists
        companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        // Check whether the recruiter is an active member
        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this company"
            );
        }

        List<CompanyMembershipRequest> invitations;

        if (filter == MembershipRequestFilter.ALL) {

            invitations = companyMembershipRequestRepository
                    .findAllByCompanyIdAndRequestType(
                            companyId,
                            MembershipRequestType.INVITATION
                    );

        } else {

            invitations = companyMembershipRequestRepository
                    .findAllByCompanyIdAndRequestTypeAndRequestStatus(
                            companyId,
                            MembershipRequestType.INVITATION,
                            MembershipRequestStatus.valueOf(filter.name())
                    );
        }

        return invitations.stream()
                .map(companyMapper::toMembershipRequestResponse)
                .toList();
    }


    @Transactional
    public MembershipRequestResponse decideInvitation(
            Long companyId,
            Long requestId,
            DecideInvitationRequest request,
            User recruiter) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can respond to company invitations"
            );
        }

        if (request.getRequestStatus() != MembershipRequestStatus.APPROVED
                && request.getRequestStatus() != MembershipRequestStatus.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invitation can only be accepted or rejected"
            );
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        CompanyMembershipRequest invitation =
                companyMembershipRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Invitation does not exist"
                        ));

        if (!invitation.getCompany().getId().equals(company.getId())
                || invitation.getRequestType() != MembershipRequestType.INVITATION) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Invitation does not belong to this company"
            );
        }

        if (!(invitation.getTargetRecruiter().getId()==recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This invitation was not sent to you"
            );
        }

        if (invitation.getRequestStatus() != MembershipRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This invitation has already been decided"
            );
        }

        invitation.markAsDecided(recruiter, request.getRequestStatus());

        if (request.getRequestStatus() == MembershipRequestStatus.APPROVED) {
            CompanyRecruiter membership =
                    new CompanyRecruiter(company, recruiter);

            companyRecruiterRepository.save(membership);
        }

        CompanyMembershipRequest savedInvitation =
                companyMembershipRequestRepository.save(invitation);

        return companyMapper.toMembershipRequestResponse(savedInvitation);
    }

    public List<CompanyRecruiterResponse> getCompanyRecruiters(
            Long companyId, User recruiter) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view company recruiters"
            );
        }

        companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this company"
            );
        }

        List<CompanyRecruiter> memberships =
                companyRecruiterRepository.findAllByCompanyId(companyId);

        return memberships.stream().map(membership -> {
            CompanyRecruiterResponse response = new CompanyRecruiterResponse();

            response.setRecruiterId(membership.getRecruiter().getId());
            response.setFullName(membership.getRecruiter().getFullName());

            return response;
        }).toList();
    }

    @Transactional
    public void removeRecruiter(
            Long companyId,
            Long recruiterId,
            User recruiter) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can remove company members"
            );
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company does not exist"
                ));

        // Only the company owner can remove recruiters
        if (!(company.getCreatedBy().getId() == recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the company owner can remove recruiters"
            );
        }

        // Owner cannot remove themselves
        if (recruiterId.equals(company.getCreatedBy().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The company owner cannot remove themselves"
            );
        }

        CompanyRecruiter membership =
                companyRecruiterRepository
                        .findByCompanyIdAndRecruiterId(companyId, recruiterId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Recruiter is not a member of this company"
                        ));

        companyRecruiterRepository.delete(membership);
    }

    public List<MembershipRequestResponse> getMyJoinRequests(
            User recruiter,
            MembershipRequestFilter filter) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view their join requests"
            );
        }

        List<CompanyMembershipRequest> requests;

        if (filter == MembershipRequestFilter.ALL) {

            requests = companyMembershipRequestRepository
                    .findAllByTargetRecruiterIdAndRequestType(
                            recruiter.getId(),
                            MembershipRequestType.JOIN_REQUEST
                    );

        } else {

            requests = companyMembershipRequestRepository
                    .findAllByTargetRecruiterIdAndRequestTypeAndRequestStatus(
                            recruiter.getId(),
                            MembershipRequestType.JOIN_REQUEST,
                            MembershipRequestStatus.valueOf(filter.name())
                    );
        }

        return requests.stream()
                .map(companyMapper::toMembershipRequestResponse)
                .toList();
    }

    public List<MembershipRequestResponse> getMyInvitations(
            User recruiter,
            MembershipRequestFilter filter) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view their invitations"
            );
        }

        List<CompanyMembershipRequest> invitations;

        if (filter == MembershipRequestFilter.ALL) {

            invitations = companyMembershipRequestRepository
                    .findAllByTargetRecruiterIdAndRequestType(
                            recruiter.getId(),
                            MembershipRequestType.INVITATION
                    );

        } else {

            invitations = companyMembershipRequestRepository
                    .findAllByTargetRecruiterIdAndRequestTypeAndRequestStatus(
                            recruiter.getId(),
                            MembershipRequestType.INVITATION,
                            MembershipRequestStatus.valueOf(filter.name())
                    );
        }

        return invitations.stream()
                .map(companyMapper::toMembershipRequestResponse)
                .toList();
    }
}
