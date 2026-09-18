package com.govskill.models;

import java.util.ArrayList;
import java.util.List;

public class StudentProfile {
    private int id;
    private int userId;
    private String dateOfBirth;
    private String gender;
    private String category;
    private String branch;
    private String educationLevel;
    private String college;
    private Integer graduationYear;
    private String bio;
    private boolean profileVisibility = true;
    private boolean consentGiven = true;
    private int profileCompletion = 20;

    // Joined fields for display
    private String name;
    private String email;
    private String phone;
    private String state;
    private String city;

    private List<Education> educationList = new ArrayList<>();
    private List<Skill> skillsList = new ArrayList<>();
    private List<Certificate> certificatesList = new ArrayList<>();

    public StudentProfile() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isProfileVisibility() { return profileVisibility; }
    public void setProfileVisibility(boolean profileVisibility) { this.profileVisibility = profileVisibility; }

    public boolean isConsentGiven() { return consentGiven; }
    public void setConsentGiven(boolean consentGiven) { this.consentGiven = consentGiven; }

    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<Education> getEducationList() { return educationList; }
    public void setEducationList(List<Education> educationList) { this.educationList = educationList; }

    public List<Skill> getSkillsList() { return skillsList; }
    public void setSkillsList(List<Skill> skillsList) { this.skillsList = skillsList; }

    public List<Certificate> getCertificatesList() { return certificatesList; }
    public void setCertificatesList(List<Certificate> certificatesList) { this.certificatesList = certificatesList; }
}
