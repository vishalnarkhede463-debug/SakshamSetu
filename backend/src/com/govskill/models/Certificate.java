package com.govskill.models;

public class Certificate {
    private int id;
    private int studentProfileId;
    private Integer skillId;
    private String skillName; // Joined from skills
    private String name;
    private String issuingOrganization;
    private String certificateUrl;
    private String issueDate;

    public Certificate() {}

    public Certificate(int id, int studentProfileId, Integer skillId, String name, String issuingOrganization, String certificateUrl, String issueDate) {
        this.id = id;
        this.studentProfileId = studentProfileId;
        this.skillId = skillId;
        this.name = name;
        this.issuingOrganization = issuingOrganization;
        this.certificateUrl = certificateUrl;
        this.issueDate = issueDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentProfileId() { return studentProfileId; }
    public void setStudentProfileId(int studentProfileId) { this.studentProfileId = studentProfileId; }

    public Integer getSkillId() { return skillId; }
    public void setSkillId(Integer skillId) { this.skillId = skillId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIssuingOrganization() { return issuingOrganization; }
    public void setIssuingOrganization(String issuingOrganization) { this.issuingOrganization = issuingOrganization; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
}
