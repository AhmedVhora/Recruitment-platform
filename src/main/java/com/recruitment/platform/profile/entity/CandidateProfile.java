
package com.recruitment.platform.profile.entity;

import com.recruitment.platform.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User relationship
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    // Basic professional information
    @Column(length = 255)
    private String headline;

    @Column(length = 2000)
    private String professionalSummary;

    @Column(length = 255)
    private String location;

    private Integer yearsOfExperience;

    @Column(length = 500)
    private String linkedinUrl;

    @Column(length = 500)
    private String githubUrl;

    @Column(length = 500)
    private String portfolioUrl;

    // Skills
    @ElementCollection
    @CollectionTable(
            name = "candidate_profile_skills",
            joinColumns = @JoinColumn(name = "candidate_profile_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"candidate_profile_id", "skill"}
            )
    )
    @Column(name = "skill", nullable = false)
    private Set<String> skills = new HashSet<>();

    // Resume URLs
    @ElementCollection
    @CollectionTable(
            name = "candidate_profile_resume_urls",
            joinColumns = @JoinColumn(name = "candidate_profile_id")
    )
    @OrderColumn(name = "resume_order")
    @Column(name = "resume_url", nullable = false, length = 2048)
    private List<String> resumeUrls = new ArrayList<>();

    // Projects
    @OneToMany(
            mappedBy = "candidateProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CandidateProject> projects = new ArrayList<>();

    // Certifications
    @OneToMany(
            mappedBy = "candidateProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CandidateCertification> certifications = new ArrayList<>();

    // Education
    @OneToMany(
            mappedBy = "candidateProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CandidateEducation> education = new ArrayList<>();

    // Work experience
    @OneToMany(
            mappedBy = "candidateProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CandidateExperience> experiences = new ArrayList<>();

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public CandidateProfile() {
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public List<String> getResumeUrls() {
        return resumeUrls;
    }

    public void setResumeUrls(List<String> resumeUrls) {
        this.resumeUrls = resumeUrls;
    }

    public List<CandidateProject> getProjects() {
        return projects;
    }

    public void setProjects(List<CandidateProject> projects) {
        this.projects = projects;
    }

    public List<CandidateCertification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CandidateCertification> certifications) {
        this.certifications = certifications;
    }

    public List<CandidateEducation> getEducation() {
        return education;
    }

    public void setEducation(List<CandidateEducation> education) {
        this.education = education;
    }

    public List<CandidateExperience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<CandidateExperience> experiences) {
        this.experiences = experiences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
