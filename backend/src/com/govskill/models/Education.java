package com.govskill.models;

public class Education {
    private int id;
    private int studentProfileId;
    private String qualification;
    private String institution;
    private String branch;
    private double percentage;
    private int passingYear;

    public Education() {}

    public Education(int id, int studentProfileId, String qualification, String institution, String branch, double percentage, int passingYear) {
        this.id = id;
        this.studentProfileId = studentProfileId;
        this.qualification = qualification;
        this.institution = institution;
        this.branch = branch;
        this.percentage = percentage;
        this.passingYear = passingYear;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentProfileId() { return studentProfileId; }
    public void setStudentProfileId(int studentProfileId) { this.studentProfileId = studentProfileId; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public int getPassingYear() { return passingYear; }
    public void setPassingYear(int passingYear) { this.passingYear = passingYear; }
}
