
package com.recruitment.platform.company.controller;

import com.recruitment.platform.company.dto.*;
import com.recruitment.platform.company.entity.MembershipRequestFilter;
import com.recruitment.platform.company.service.CompanyService;
import com.recruitment.platform.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create_company")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse createCompany(
            @Valid @RequestBody CreateCompanyRequest request,
            @AuthenticationPrincipal User user) {

        return companyService.createCompany(request, user);
    }

    @PatchMapping("/update_company/{company_id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse updateCompany(
            @PathVariable("company_id") Long companyId,
            @Valid @RequestBody UpdateCompanyRequest request,
            @AuthenticationPrincipal User user) {

        return companyService.updateCompany(companyId, request, user);
    }

    @GetMapping("/all_companies")
    @ResponseStatus(HttpStatus.OK)
    public Page<CompanyResponse> getAllCompanies(
            @PageableDefault(
                    size = 10,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        return companyService.getAllCompanies(pageable);
    }

    @GetMapping("/{company_id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse getCompanyById(
            @PathVariable("company_id") Long companyId) {

        return companyService.getCompanyById(companyId);
    }

    @GetMapping("/search")
    public Page<CompanyResponse> searchCompanies(
            @RequestParam String name,
            @PageableDefault(
                    size = 10,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        return companyService.searchCompanies(name, pageable);
    }

    @GetMapping("/my_recruiter_companies")
    public List<CompanyResponse> getMyRecruiterCompanies(
            @AuthenticationPrincipal User user) {

        return companyService.getMyRecruiterCompanies(user);
    }

    @GetMapping("/my_owned_companies")
    public List<CompanyResponse> getMyOwnedCompanies(
            @AuthenticationPrincipal User user) {

        return companyService.getMyOwnedCompanies(user);
    }

    @PostMapping("/{companyId}/join-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipRequestResponse createJoinRequest(
            @PathVariable Long companyId,
            @AuthenticationPrincipal User recruiter) {

        return companyService.createJoinRequest(companyId, recruiter);
    }

    @GetMapping("/{companyId}/join-requests")
    @ResponseStatus(HttpStatus.OK)
    public List<MembershipRequestResponse> getCompanyJoinRequests(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "ALL") MembershipRequestFilter filter,
            @AuthenticationPrincipal User recruiter) {

        return companyService.getCompanyJoinRequests(
                companyId, recruiter, filter
        );
    }

    @PatchMapping("/{companyId}/join-requests/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public MembershipRequestResponse decideJoinRequest(
            @PathVariable Long companyId,
            @PathVariable Long requestId,
            @Valid @RequestBody DecideMembershipRequest request,
            @AuthenticationPrincipal User recruiter) {

        return companyService.decideJoinRequest(
                companyId, requestId, request, recruiter
        );
    }

    @PostMapping("/{companyId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipRequestResponse sendInvitation(
            @PathVariable Long companyId,
            @Valid @RequestBody InviteRecruiterRequest request,
            @AuthenticationPrincipal User recruiter) {

        return companyService.sendInvitation(companyId, request, recruiter);
    }

    @GetMapping("/{companyId}/invitations")
    @ResponseStatus(HttpStatus.OK)
    public List<MembershipRequestResponse> getCompanyInvitations(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "ALL") MembershipRequestFilter filter,
            @AuthenticationPrincipal User recruiter) {

        return companyService.getCompanyInvitations(
                companyId, recruiter, filter
        );
    }

    @PatchMapping("/{companyId}/invitations/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public MembershipRequestResponse decideInvitation(
            @PathVariable Long companyId,
            @PathVariable Long requestId,
            @Valid @RequestBody DecideInvitationRequest request,
            @AuthenticationPrincipal User recruiter) {

        return companyService.decideInvitation(
                companyId, requestId, request, recruiter
        );
    }

    @GetMapping("/{companyId}/recruiters")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyRecruiterResponse> getCompanyRecruiters(
            @PathVariable Long companyId,
            @AuthenticationPrincipal User recruiter) {

        return companyService.getCompanyRecruiters(companyId, recruiter);
    }

    @DeleteMapping("/{companyId}/recruiters/{recruiterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRecruiter(
            @PathVariable Long companyId,
            @PathVariable Long recruiterId,
            @AuthenticationPrincipal User recruiter) {

        companyService.removeRecruiter(companyId, recruiterId, recruiter);
    }

    @GetMapping("/my_join_requests")
    @ResponseStatus(HttpStatus.OK)
    public List<MembershipRequestResponse> getMyJoinRequests(
            @RequestParam(defaultValue = "ALL") MembershipRequestFilter filter,
            @AuthenticationPrincipal User recruiter) {

        return companyService.getMyJoinRequests(recruiter, filter);
    }

    @GetMapping("/my_invitations")
    @ResponseStatus(HttpStatus.OK)
    public List<MembershipRequestResponse> getMyInvitations(
            @RequestParam(defaultValue = "ALL") MembershipRequestFilter filter,
            @AuthenticationPrincipal User recruiter) {

        return companyService.getMyInvitations(recruiter, filter);
    }
}
