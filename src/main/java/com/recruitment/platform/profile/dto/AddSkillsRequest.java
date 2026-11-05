package com.recruitment.platform.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class AddSkillsRequest {

    @NotEmpty
    private Set<@NotBlank String> skills;

    public AddSkillsRequest() {
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }
}