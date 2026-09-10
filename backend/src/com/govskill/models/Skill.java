package com.govskill.models;

public class Skill {
    private int id;
    private int studentProfileId;
    private String name;
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    private boolean verified;

    public Skill() {}

    public Skill(int id, int studentProfileId, String name, String level, boolean verified) {
        this.id = id;
        this.studentProfileId = studentProfileId;
        this.name = name;
        this.level = level;
        this.verified = verified;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentProfileId() { return studentProfileId; }
    public void setStudentProfileId(int studentProfileId) { this.studentProfileId = studentProfileId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
