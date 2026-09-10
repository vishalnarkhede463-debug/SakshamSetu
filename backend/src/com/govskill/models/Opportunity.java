package com.govskill.models;

public class Opportunity {
    private int id;
    private Integer employerId;
    private String title;
    private String description;
    private String type; // GOVERNMENT_JOB, PRIVATE_JOB, INTERNSHIP, APPRENTICESHIP, SCHOLARSHIP, SKILL_DEVELOPMENT
    private String organization;
    private String location;
    private String state;
    private String city;
    private String branch;
    private String educationRequirement;
    private String skills;
    private String category;
    private String stipendOrSalary;
    private String deadline;
    private String sourceUrl;
    private boolean verified;
    private String createdAt;

    // Transient computed field
    private int matchScore = 0;

    public Opportunity() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getEmployerId() { return employerId; }
    public void setEmployerId(Integer employerId) { this.employerId = employerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getEducationRequirement() { return educationRequirement; }
    public void setEducationRequirement(String educationRequirement) { this.educationRequirement = educationRequirement; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStipendOrSalary() { return stipendOrSalary; }
    public void setStipendOrSalary(String stipendOrSalary) { this.stipendOrSalary = stipendOrSalary; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }
}
