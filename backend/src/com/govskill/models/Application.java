package com.govskill.models;

public class Application {
    private int id;
    private int opportunityId;
    private int studentId;
    private String status; // APPLIED, UNDER_REVIEW, SHORTLISTED, REJECTED, SELECTED
    private String coverNote;
    private String appliedAt;
    private String updatedAt;

    // Joined fields for student view
    private String opportunityTitle;
    private String organization;
    private String opportunityType;
    private String location;
    private String stipendOrSalary;
    private String deadline;

    // Joined fields for employer view
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String studentBranch;
    private String studentEducationLevel;
    private String studentCollege;
    private String studentCity;
    private int studentMatchScore = 0;

    public Application() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCoverNote() { return coverNote; }
    public void setCoverNote(String coverNote) { this.coverNote = coverNote; }

    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getOpportunityTitle() { return opportunityTitle; }
    public void setOpportunityTitle(String opportunityTitle) { this.opportunityTitle = opportunityTitle; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getOpportunityType() { return opportunityType; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStipendOrSalary() { return stipendOrSalary; }
    public void setStipendOrSalary(String stipendOrSalary) { this.stipendOrSalary = stipendOrSalary; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentPhone() { return studentPhone; }
    public void setPhone(String studentPhone) { this.studentPhone = studentPhone; }

    public String getStudentBranch() { return studentBranch; }
    public void setStudentBranch(String studentBranch) { this.studentBranch = studentBranch; }

    public String getStudentEducationLevel() { return studentEducationLevel; }
    public void setStudentEducationLevel(String studentEducationLevel) { this.studentEducationLevel = studentEducationLevel; }

    public String getStudentCollege() { return studentCollege; }
    public void setStudentCollege(String studentCollege) { this.studentCollege = studentCollege; }

    public String getStudentCity() { return studentCity; }
    public void setStudentCity(String studentCity) { this.studentCity = studentCity; }

    public int getStudentMatchScore() { return studentMatchScore; }
    public void setStudentMatchScore(int studentMatchScore) { this.studentMatchScore = studentMatchScore; }
}
