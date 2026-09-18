package com.govskill.models;

import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String salt;
    private String role; // STUDENT, EMPLOYER, ADMIN
    private String state;
    private String city;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String name, String email, String phone, String passwordHash, String salt, String role, String state, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.state = state;
        this.city = city;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
