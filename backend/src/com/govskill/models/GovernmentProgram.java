package com.govskill.models;

public class GovernmentProgram {
    private int id;
    private String name;
    private String department;
    private String type; // SCHOLARSHIP, SKILL_DEVELOPMENT, EMPLOYMENT_SCHEME, APPRENTICESHIP
    private String description;
    private String eligibility;
    private String benefits;
    private String applicationUrl;
    private String deadline;

    public GovernmentProgram() {}

    public GovernmentProgram(int id, String name, String department, String type, String description, String eligibility, String benefits, String applicationUrl, String deadline) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.type = type;
        this.description = description;
        this.eligibility = eligibility;
        this.benefits = benefits;
        this.applicationUrl = applicationUrl;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getApplicationUrl() { return applicationUrl; }
    public void setApplicationUrl(String applicationUrl) { this.applicationUrl = applicationUrl; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
