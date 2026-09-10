package com.govskill.services;

import com.govskill.dao.EmployerDAO;
import com.govskill.dao.StudentDAO;
import com.govskill.dao.UserDAO;
import com.govskill.models.EmployerProfile;
import com.govskill.models.StudentProfile;
import com.govskill.models.User;
import com.govskill.utils.PasswordUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final EmployerDAO employerDAO = new EmployerDAO();

    // In-memory active session token store: token -> user
    public static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public static class AuthResult {
        public boolean success;
        public String message;
        public String token;
        public User user;
        public Object profile;

        public AuthResult(boolean success, String message, String token, User user, Object profile) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.user = user;
            this.profile = profile;
        }
    }

    public AuthResult login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return new AuthResult(false, "Email and password are required.", null, null, null);
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            return new AuthResult(false, "Invalid email or password.", null, null, null);
        }

        boolean valid = PasswordUtil.verifyPassword(password.trim(), user.getSalt(), user.getPasswordHash());
        if (!valid) {
            return new AuthResult(false, "Invalid email or password.", null, null, null);
        }

        String token = UUID.randomUUID().toString();
        sessions.put(token, user);

        Object profile = null;
        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            profile = studentDAO.findByUserId(user.getId());
        } else if ("EMPLOYER".equalsIgnoreCase(user.getRole())) {
            profile = employerDAO.findByUserId(user.getId());
        }

        return new AuthResult(true, "Login successful.", token, user, profile);
    }

    public AuthResult registerStudent(String name, String email, String phone, String password, String state, String city) {
        if (name == null || email == null || phone == null || password == null ||
            name.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty() || password.trim().isEmpty()) {
            return new AuthResult(false, "All required fields must be completed.", null, null, null);
        }

        if (userDAO.findByEmail(email.trim()) != null) {
            return new AuthResult(false, "An account with this email already exists.", null, null, null);
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password.trim(), salt);

        User user = new User(0, name.trim(), email.trim(), phone.trim(), hash, salt, "STUDENT",
                state != null ? state.trim() : "Maharashtra",
                city != null ? city.trim() : "Pune");

        user = userDAO.create(user);

        // Initialize Student Profile
        StudentProfile sp = new StudentProfile();
        sp.setUserId(user.getId());
        sp.setProfileCompletion(20);
        sp.setConsentGiven(true);
        sp.setProfileVisibility(true);
        sp = studentDAO.create(sp);

        String token = UUID.randomUUID().toString();
        sessions.put(token, user);

        return new AuthResult(true, "Student registered successfully.", token, user, sp);
    }

    public AuthResult registerEmployer(String orgName, String email, String phone, String password, String orgType, String state, String city) {
        if (orgName == null || email == null || phone == null || password == null ||
            orgName.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty() || password.trim().isEmpty()) {
            return new AuthResult(false, "All required fields must be completed.", null, null, null);
        }

        if (userDAO.findByEmail(email.trim()) != null) {
            return new AuthResult(false, "An account with this official email already exists.", null, null, null);
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password.trim(), salt);

        User user = new User(0, orgName.trim(), email.trim(), phone.trim(), hash, salt, "EMPLOYER",
                state != null ? state.trim() : "Delhi",
                city != null ? city.trim() : "New Delhi");

        user = userDAO.create(user);

        // Initialize Employer Profile
        EmployerProfile ep = new EmployerProfile();
        ep.setUserId(user.getId());
        ep.setOrganizationName(orgName.trim());
        ep.setOrganizationType(orgType != null ? orgType.trim() : "PRIVATE");
        ep.setVerificationStatus("PENDING");
        ep = employerDAO.create(ep);

        String token = UUID.randomUUID().toString();
        sessions.put(token, user);

        return new AuthResult(true, "Employer registered successfully.", token, user, ep);
    }

    public boolean logout(String token) {
        if (token == null) return false;
        return sessions.remove(token) != null;
    }

    public User getUserByToken(String token) {
        if (token == null) return null;
        return sessions.get(token);
    }
}
