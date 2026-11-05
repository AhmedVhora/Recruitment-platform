package com.recruitment.platform.profile.controller;

import com.recruitment.platform.profile.dto.*;
import com.recruitment.platform.profile.service.CandidateProfileService;
import com.recruitment.platform.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    public CandidateProfileController(CandidateProfileService candidateProfileService) {
        this.candidateProfileService = candidateProfileService;
    }

    // Create Candidate Profile
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateProfileResponse createProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCandidateProfileRequest request) {

        return candidateProfileService.createProfile(user, request);
    }

    // Get Candidate Profile
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse getProfile(
            @AuthenticationPrincipal User user) {

        return candidateProfileService.getProfile(user);
    }

    // Update Candidate Profile
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateCandidateProfileRequest request) {

        return candidateProfileService.updateProfile(user, request);
    }


    // Add Skills
    @PostMapping("/me/skills")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse addSkills(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddSkillsRequest request) {

        return candidateProfileService.addSkills(user, request);
    }

    // Remove Skill
    @DeleteMapping("/me/skills/{skill}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSkill(
            @AuthenticationPrincipal User user,
            @PathVariable String skill) {

        candidateProfileService.removeSkill(user, skill);
    }

    @PostMapping("/me/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateProfileResponse addProject(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddCandidateProjectRequest request) {

        return candidateProfileService.addProject(user, request);
    }

    @PatchMapping("/me/projects/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse updateProject(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateCandidateProjectRequest request) {

        return candidateProfileService.updateProject(user, projectId, request);
    }

    @DeleteMapping("/me/projects/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId) {

        candidateProfileService.deleteProject(user, projectId);
    }

    @GetMapping("/{candidateId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse getCandidateProfile(
            @AuthenticationPrincipal User user,
            @PathVariable Long candidateId) {

        return candidateProfileService.getCandidateProfile(user, candidateId);
    }

    // Add Certification
    @PostMapping("/me/certifications")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateProfileResponse addCertification(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddCandidateCertificationRequest request) {

        return candidateProfileService.addCertification(user, request);
    }

    // Update Certification
    @PatchMapping("/me/certifications/{certificationId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse updateCertification(
            @AuthenticationPrincipal User user,
            @PathVariable Long certificationId,
            @Valid @RequestBody UpdateCandidateCertificationRequest request) {

        return candidateProfileService.updateCertification(
                user, certificationId, request
        );
    }

    // Delete Certification
    @DeleteMapping("/me/certifications/{certificationId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse deleteCertification(
            @AuthenticationPrincipal User user,
            @PathVariable Long certificationId) {

        return candidateProfileService.deleteCertification(user, certificationId);
    }

    // Add Education
    @PostMapping("/me/education")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateProfileResponse addEducation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddCandidateEducationRequest request) {

        return candidateProfileService.addEducation(user, request);
    }

    // Update Education
    @PatchMapping("/me/education/{educationId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse updateEducation(
            @AuthenticationPrincipal User user,
            @PathVariable Long educationId,
            @Valid @RequestBody UpdateCandidateEducationRequest request) {

        return candidateProfileService.updateEducation(
                user,
                educationId,
                request
        );


    }

    // Delete Education
    @DeleteMapping("/me/education/{educationId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse deleteEducation(
            @AuthenticationPrincipal User user,
            @PathVariable Long educationId) {

        return candidateProfileService.deleteEducation(user, educationId);
    }

    // Add Work Experience
    @PostMapping("/me/experiences")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateProfileResponse addExperience(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddCandidateExperienceRequest request) {

        return candidateProfileService.addExperience(user, request);
    }

    // Update Work Experience
    @PatchMapping("/me/experiences/{experienceId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse updateExperience(
            @AuthenticationPrincipal User user,
            @PathVariable Long experienceId,
            @Valid @RequestBody UpdateCandidateExperienceRequest request) {

        return candidateProfileService.updateExperience(
                user,
                experienceId,
                request
        );
    }

    // Delete Work Experience
    @DeleteMapping("/me/experiences/{experienceId}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateProfileResponse deleteExperience(
            @AuthenticationPrincipal User user,
            @PathVariable Long experienceId) {

        return candidateProfileService.deleteExperience(user, experienceId);
    }

    // Delete Candidate Profile
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(
            @AuthenticationPrincipal User user) {

        candidateProfileService.deleteProfile(user);
    }


}