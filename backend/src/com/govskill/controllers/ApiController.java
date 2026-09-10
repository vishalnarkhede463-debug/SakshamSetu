package com.govskill.controllers;

import com.govskill.dao.*;
import com.govskill.models.*;
import com.govskill.services.AuthService;
import com.govskill.services.EligibilityService;
import com.govskill.services.MatchingService;
import com.govskill.services.TrustedExecutionEnvironment;
import com.govskill.utils.FileStorageUtil;
import com.govskill.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiController {

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final EmployerDAO employerDAO = new EmployerDAO();
    private final OpportunityDAO opportunityDAO = new OpportunityDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final CertificateDAO certificateDAO = new CertificateDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final ProgramDAO programDAO = new ProgramDAO();
    private final EligibilityService eligibilityService = new EligibilityService();

    public void handle(HttpExchange exchange) throws IOException {
        // Enable CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        String method = exchange.getRequestMethod().toUpperCase();
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> qParams = parseQueryParams(query);

        // Get authenticated user
        User currentUser = getAuthenticatedUser(exchange);

        try {
            if (path.startsWith("/api/auth/")) {
                handleAuth(exchange, path, method);
            } else if (path.startsWith("/api/opportunities")) {
                handleOpportunities(exchange, path, method, qParams, currentUser);
            } else if (path.startsWith("/api/student/")) {
                handleStudent(exchange, path, method, currentUser);
            } else if (path.startsWith("/api/applications")) {
                handleApplications(exchange, path, method, currentUser);
            } else if (path.startsWith("/api/saved-opportunities")) {
                handleSavedOpportunities(exchange, path, method, qParams, currentUser);
            } else if (path.startsWith("/api/employer/")) {
                handleEmployer(exchange, path, method, qParams, currentUser);
            } else if (path.startsWith("/api/eligibility")) {
                handleEligibility(exchange, method, qParams);
            } else if (path.startsWith("/api/government-programs")) {
                handleGovernmentPrograms(exchange, method, qParams);
            } else if (path.startsWith("/api/notifications")) {
                handleNotifications(exchange, path, method, currentUser);
            } else if (path.startsWith("/api/admin/")) {
                handleAdmin(exchange, path, method, currentUser);
            } else if (path.startsWith("/api/security/tee-status")) {
                handleTeeStatus(exchange, method);
            } else {
                sendJsonResponse(exchange, 404, Map.of("error", "API endpoint not found: " + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // Auth Handlers
    // -------------------------------------------------------------------------
    private void handleAuth(HttpExchange exchange, String path, String method) throws IOException {
        if ("/api/auth/login".equals(path) && "POST".equals(method)) {
            Map<String, Object> body = readJsonBody(exchange);
            String email = JsonUtil.getString(body, "email", "");
            String password = JsonUtil.getString(body, "password", "");
            AuthService.AuthResult res = authService.login(email, password);
            if (res.success) {
                sendJsonResponse(exchange, 200, Map.of(
                        "success", true,
                        "message", res.message,
                        "token", res.token,
                        "user", res.user,
                        "profile", res.profile != null ? res.profile : ""
                ));
            } else {
                sendJsonResponse(exchange, 401, Map.of("success", false, "error", res.message));
            }
        } else if ("/api/auth/register".equals(path) && "POST".equals(method)) {
            Map<String, Object> body = readJsonBody(exchange);
            String role = JsonUtil.getString(body, "role", "STUDENT").toUpperCase();

            AuthService.AuthResult res;
            if ("EMPLOYER".equals(role)) {
                res = authService.registerEmployer(
                        JsonUtil.getString(body, "organizationName", ""),
                        JsonUtil.getString(body, "email", ""),
                        JsonUtil.getString(body, "phone", ""),
                        JsonUtil.getString(body, "password", ""),
                        JsonUtil.getString(body, "organizationType", "PRIVATE"),
                        JsonUtil.getString(body, "state", "Maharashtra"),
                        JsonUtil.getString(body, "city", "Mumbai")
                );
            } else {
                res = authService.registerStudent(
                        JsonUtil.getString(body, "name", ""),
                        JsonUtil.getString(body, "email", ""),
                        JsonUtil.getString(body, "phone", ""),
                        JsonUtil.getString(body, "password", ""),
                        JsonUtil.getString(body, "state", "Maharashtra"),
                        JsonUtil.getString(body, "city", "Pune")
                );
            }

            if (res.success) {
                sendJsonResponse(exchange, 201, Map.of(
                        "success", true,
                        "message", res.message,
                        "token", res.token,
                        "user", res.user,
                        "profile", res.profile != null ? res.profile : ""
                ));
            } else {
                sendJsonResponse(exchange, 400, Map.of("success", false, "error", res.message));
            }
        } else if ("/api/auth/logout".equals(path) && "POST".equals(method)) {
            String token = getBearerToken(exchange);
            authService.logout(token);
            sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Logged out successfully."));
        } else if ("/api/auth/me".equals(path) && "GET".equals(method)) {
            User user = getAuthenticatedUser(exchange);
            if (user == null) {
                sendJsonResponse(exchange, 401, Map.of("error", "Not authenticated."));
                return;
            }
            Object profile = null;
            if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                profile = studentDAO.findByUserId(user.getId());
            } else if ("EMPLOYER".equalsIgnoreCase(user.getRole())) {
                profile = employerDAO.findByUserId(user.getId());
            }
            sendJsonResponse(exchange, 200, Map.of(
                    "user", user,
                    "profile", profile != null ? profile : ""
            ));
        } else {
            sendJsonResponse(exchange, 405, Map.of("error", "Method not allowed."));
        }
    }

    // -------------------------------------------------------------------------
    // Opportunities Handlers
    // -------------------------------------------------------------------------
    private void handleOpportunities(HttpExchange exchange, String path, String method,
                                     Map<String, String> qParams, User currentUser) throws IOException {
        String subPath = path.substring("/api/opportunities".length());

        if (subPath.isEmpty() || "/".equals(subPath)) {
            if ("GET".equals(method)) {
                String q = qParams.get("q");
                String type = qParams.get("type");
                String state = qParams.get("state");
                String city = qParams.get("city");
                String branch = qParams.get("branch");
                String edu = qParams.get("education");
                String skill = qParams.get("skill");
                String cat = qParams.get("category");

                List<Opportunity> list = opportunityDAO.search(q, type, state, city, branch, edu, skill, cat);

                // If logged in student, calculate match score and ranking!
                if (currentUser != null && "STUDENT".equalsIgnoreCase(currentUser.getRole())) {
                    StudentProfile sp = studentDAO.findByUserId(currentUser.getId());
                    if (sp != null) {
                        list = MatchingService.rankOpportunitiesForStudent(sp, list);
                    }
                }

                sendJsonResponse(exchange, 200, Map.of("opportunities", list, "count", list.size()));
            } else if ("POST".equals(method)) {
                if (currentUser == null || (!"EMPLOYER".equalsIgnoreCase(currentUser.getRole()) && !"ADMIN".equalsIgnoreCase(currentUser.getRole()))) {
                    sendJsonResponse(exchange, 403, Map.of("error", "Only employers and admins can post opportunities."));
                    return;
                }
                EmployerProfile ep = employerDAO.findByUserId(currentUser.getId());
                Map<String, Object> body = readJsonBody(exchange);

                Opportunity opp = new Opportunity();
                if (ep != null) opp.setEmployerId(ep.getId());
                opp.setTitle(JsonUtil.getString(body, "title", ""));
                opp.setDescription(JsonUtil.getString(body, "description", ""));
                opp.setType(JsonUtil.getString(body, "type", "PRIVATE_JOB"));
                opp.setOrganization(ep != null ? ep.getOrganizationName() : JsonUtil.getString(body, "organization", "GovSkill"));
                opp.setLocation(JsonUtil.getString(body, "location", "Pune"));
                opp.setState(JsonUtil.getString(body, "state", "Maharashtra"));
                opp.setCity(JsonUtil.getString(body, "city", "Pune"));
                opp.setBranch(JsonUtil.getString(body, "branch", "All"));
                opp.setEducationRequirement(JsonUtil.getString(body, "educationRequirement", "B.Tech / B.E."));
                opp.setSkills(JsonUtil.getString(body, "skills", "Java"));
                opp.setCategory(JsonUtil.getString(body, "category", "ALL"));
                opp.setStipendOrSalary(JsonUtil.getString(body, "stipendOrSalary", "As per norms"));
                opp.setDeadline(JsonUtil.getString(body, "deadline", "2026-12-31"));
                opp.setSourceUrl(JsonUtil.getString(body, "sourceUrl", ""));
                opp.setVerified(true);

                opp = opportunityDAO.create(opp);
                sendJsonResponse(exchange, 201, Map.of("success", true, "opportunity", opp));
            } else {
                sendJsonResponse(exchange, 405, Map.of("error", "Method not allowed."));
            }
        } else {
            // /api/opportunities/{id}
            String idStr = subPath.startsWith("/") ? subPath.substring(1) : subPath;
            int oppId;
            try {
                oppId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                sendJsonResponse(exchange, 400, Map.of("error", "Invalid opportunity ID."));
                return;
            }

            Opportunity opp = opportunityDAO.findById(oppId);
            if (opp == null) {
                sendJsonResponse(exchange, 404, Map.of("error", "Opportunity not found."));
                return;
            }

            if ("GET".equals(method)) {
                int matchScore = 0;
                boolean isSaved = false;
                boolean hasApplied = false;

                if (currentUser != null && "STUDENT".equalsIgnoreCase(currentUser.getRole())) {
                    StudentProfile sp = studentDAO.findByUserId(currentUser.getId());
                    if (sp != null) {
                        matchScore = MatchingService.calculateMatch(sp, opp);
                        isSaved = opportunityDAO.isOpportunitySaved(sp.getId(), oppId);
                        hasApplied = applicationDAO.hasApplied(sp.getId(), oppId);
                    }
                }
                opp.setMatchScore(matchScore);

                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("opportunity", opp);
                resp.put("matchScore", matchScore);
                resp.put("isSaved", isSaved);
                resp.put("hasApplied", hasApplied);
                sendJsonResponse(exchange, 200, resp);
            } else if ("PUT".equals(method)) {
                if (currentUser == null || (!"EMPLOYER".equalsIgnoreCase(currentUser.getRole()) && !"ADMIN".equalsIgnoreCase(currentUser.getRole()))) {
                    sendJsonResponse(exchange, 403, Map.of("error", "Unauthorized."));
                    return;
                }
                Map<String, Object> body = readJsonBody(exchange);
                opp.setTitle(JsonUtil.getString(body, "title", opp.getTitle()));
                opp.setDescription(JsonUtil.getString(body, "description", opp.getDescription()));
                opp.setType(JsonUtil.getString(body, "type", opp.getType()));
                opp.setLocation(JsonUtil.getString(body, "location", opp.getLocation()));
                opp.setState(JsonUtil.getString(body, "state", opp.getState()));
                opp.setCity(JsonUtil.getString(body, "city", opp.getCity()));
                opp.setBranch(JsonUtil.getString(body, "branch", opp.getBranch()));
                opp.setEducationRequirement(JsonUtil.getString(body, "educationRequirement", opp.getEducationRequirement()));
                opp.setSkills(JsonUtil.getString(body, "skills", opp.getSkills()));
                opp.setCategory(JsonUtil.getString(body, "category", opp.getCategory()));
                opp.setStipendOrSalary(JsonUtil.getString(body, "stipendOrSalary", opp.getStipendOrSalary()));
                opp.setDeadline(JsonUtil.getString(body, "deadline", opp.getDeadline()));
                opp.setSourceUrl(JsonUtil.getString(body, "sourceUrl", opp.getSourceUrl()));

                opportunityDAO.update(opp);
                sendJsonResponse(exchange, 200, Map.of("success", true, "opportunity", opp));
            } else if ("DELETE".equals(method)) {
                if (currentUser == null || (!"EMPLOYER".equalsIgnoreCase(currentUser.getRole()) && !"ADMIN".equalsIgnoreCase(currentUser.getRole()))) {
                    sendJsonResponse(exchange, 403, Map.of("error", "Unauthorized."));
                    return;
                }
                opportunityDAO.delete(oppId);
                sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Opportunity removed."));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Student Profile, Education, Skills, Certificates Handlers
    // -------------------------------------------------------------------------
    private void handleStudent(HttpExchange exchange, String path, String method, User currentUser) throws IOException {
        if (currentUser == null || !"STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            sendJsonResponse(exchange, 403, Map.of("error", "Student authorization required."));
            return;
        }

        StudentProfile sp = studentDAO.findByUserId(currentUser.getId());
        if (sp == null) {
            // Auto initialize if missing
            sp = new StudentProfile();
            sp.setUserId(currentUser.getId());
            sp = studentDAO.create(sp);
        }

        String sub = path.substring("/api/student/".length());

        if ("profile".equals(sub)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, Map.of("profile", sp));
            } else if ("POST".equals(method) || "PUT".equals(method)) {
                Map<String, Object> body = readJsonBody(exchange);
                if (body.containsKey("dateOfBirth")) sp.setDateOfBirth(JsonUtil.getString(body, "dateOfBirth", sp.getDateOfBirth()));
                if (body.containsKey("gender")) sp.setGender(JsonUtil.getString(body, "gender", sp.getGender()));
                if (body.containsKey("category")) sp.setCategory(JsonUtil.getString(body, "category", sp.getCategory()));
                if (body.containsKey("branch")) sp.setBranch(JsonUtil.getString(body, "branch", sp.getBranch()));
                if (body.containsKey("educationLevel")) sp.setEducationLevel(JsonUtil.getString(body, "educationLevel", sp.getEducationLevel()));
                if (body.containsKey("college")) sp.setCollege(JsonUtil.getString(body, "college", sp.getCollege()));
                if (body.containsKey("graduationYear")) sp.setGraduationYear(JsonUtil.getInt(body, "graduationYear", 2025));
                if (body.containsKey("bio")) sp.setBio(JsonUtil.getString(body, "bio", sp.getBio()));
                if (body.containsKey("profileVisibility")) sp.setProfileVisibility(JsonUtil.getBoolean(body, "profileVisibility", sp.isProfileVisibility()));
                if (body.containsKey("consentGiven")) sp.setConsentGiven(JsonUtil.getBoolean(body, "consentGiven", sp.isConsentGiven()));

                studentDAO.update(sp);
                sendJsonResponse(exchange, 200, Map.of("success", true, "profile", sp));
            }
        } else if ("education".equals(sub)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, Map.of("education", studentDAO.getEducation(sp.getId())));
            } else if ("POST".equals(method)) {
                Map<String, Object> body = readJsonBody(exchange);
                Education edu = new Education(
                        0,
                        sp.getId(),
                        JsonUtil.getString(body, "qualification", "Undergraduate"),
                        JsonUtil.getString(body, "institution", ""),
                        JsonUtil.getString(body, "branch", ""),
                        JsonUtil.getDouble(body, "percentage", 75.0),
                        JsonUtil.getInt(body, "passingYear", 2024)
                );
                edu = studentDAO.addEducation(edu);
                sendJsonResponse(exchange, 201, Map.of("success", true, "education", edu));
            }
        } else if (sub.startsWith("education/")) {
            int eduId = Integer.parseInt(sub.substring("education/".length()));
            if ("DELETE".equals(method)) {
                boolean ok = studentDAO.deleteEducation(eduId, sp.getId());
                sendJsonResponse(exchange, 200, Map.of("success", ok));
            }
        } else if ("skills".equals(sub)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, Map.of("skills", studentDAO.getSkills(sp.getId())));
            } else if ("POST".equals(method)) {
                Map<String, Object> body = readJsonBody(exchange);
                Skill skill = new Skill(
                        0,
                        sp.getId(),
                        JsonUtil.getString(body, "name", "Java"),
                        JsonUtil.getString(body, "level", "INTERMEDIATE"),
                        false // Explicit rule: skill cannot be verified until supporting certificate uploaded
                );
                skill = studentDAO.addSkill(skill);
                sendJsonResponse(exchange, 201, Map.of("success", true, "skill", skill));
            }
        } else if (sub.startsWith("skills/")) {
            int skillId = Integer.parseInt(sub.substring("skills/".length()));
            if ("DELETE".equals(method)) {
                boolean ok = studentDAO.deleteSkill(skillId, sp.getId());
                sendJsonResponse(exchange, 200, Map.of("success", ok));
            }
        } else if ("certificates".equals(sub)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, Map.of("certificates", certificateDAO.findByStudentProfileId(sp.getId())));
            } else if ("POST".equals(method)) {
                Map<String, Object> body = readJsonBody(exchange);
                String base64Data = JsonUtil.getString(body, "fileData", "");
                String fileName = JsonUtil.getString(body, "fileName", "cert.pdf");
                String certUrl = FileStorageUtil.saveBase64File(base64Data, fileName);

                int skillId = JsonUtil.getInt(body, "skillId", 0);
                Certificate cert = new Certificate(
                        0,
                        sp.getId(),
                        skillId > 0 ? skillId : null,
                        JsonUtil.getString(body, "name", "Professional Certificate"),
                        JsonUtil.getString(body, "issuingOrganization", "Official Board"),
                        certUrl,
                        JsonUtil.getString(body, "issueDate", "2024-01-01")
                );
                cert = certificateDAO.create(cert);
                sendJsonResponse(exchange, 201, Map.of("success", true, "certificate", cert));
            }
        } else if (sub.startsWith("certificates/")) {
            int certId = Integer.parseInt(sub.substring("certificates/".length()));
            if ("DELETE".equals(method)) {
                boolean ok = certificateDAO.delete(certId, sp.getId());
                sendJsonResponse(exchange, 200, Map.of("success", ok));
            }
        } else {
            sendJsonResponse(exchange, 404, Map.of("error", "Unknown student endpoint."));
        }
    }

    // -------------------------------------------------------------------------
    // Applications Handlers
    // -------------------------------------------------------------------------
    private void handleApplications(HttpExchange exchange, String path, String method, User currentUser) throws IOException {
        if (currentUser == null) {
            sendJsonResponse(exchange, 401, Map.of("error", "Authentication required."));
            return;
        }

        String subPath = path.substring("/api/applications".length());

        if (subPath.isEmpty() || "/".equals(subPath)) {
            if ("GET".equals(method)) {
                if ("STUDENT".equalsIgnoreCase(currentUser.getRole())) {
                    StudentProfile sp = studentDAO.findByUserId(currentUser.getId());
                    if (sp == null) {
                        sendJsonResponse(exchange, 200, Map.of("applications", Collections.emptyList()));
                        return;
                    }
                    List<Application> apps = applicationDAO.findByStudentId(sp.getId());
                    sendJsonResponse(exchange, 200, Map.of("applications", apps));
                } else if ("EMPLOYER".equalsIgnoreCase(currentUser.getRole())) {
                    EmployerProfile ep = employerDAO.findByUserId(currentUser.getId());
                    if (ep == null) {
                        sendJsonResponse(exchange, 200, Map.of("applications", Collections.emptyList()));
                        return;
                    }
                    List<Application> apps = applicationDAO.findByEmployerId(ep.getId());
                    sendJsonResponse(exchange, 200, Map.of("applications", apps));
                } else {
                    // Admin
                    sendJsonResponse(exchange, 200, Map.of("applications", applicationDAO.findAll()));
                }
            } else if ("POST".equals(method)) {
                if (!"STUDENT".equalsIgnoreCase(currentUser.getRole())) {
                    sendJsonResponse(exchange, 403, Map.of("error", "Only registered students can apply for opportunities."));
                    return;
                }
                StudentProfile sp = studentDAO.findByUserId(currentUser.getId());
                if (sp == null) {
                    sendJsonResponse(exchange, 400, Map.of("error", "Please complete your student profile before applying."));
                    return;
                }

                Map<String, Object> body = readJsonBody(exchange);
                int oppId = JsonUtil.getInt(body, "opportunityId", 0);
                String note = JsonUtil.getString(body, "coverNote", "");

                Opportunity opp = opportunityDAO.findById(oppId);
                if (opp == null) {
                    sendJsonResponse(exchange, 404, Map.of("error", "Opportunity not found."));
                    return;
                }

                if (applicationDAO.hasApplied(sp.getId(), oppId)) {
                    sendJsonResponse(exchange, 400, Map.of("error", "You have already applied for this opportunity. Duplicate applications are not allowed."));
                    return;
                }

                Application app = new Application();
                app.setOpportunityId(oppId);
                app.setStudentId(sp.getId());
                app.setStatus("APPLIED");
                app.setCoverNote(note);

                app = applicationDAO.create(app);
                sendJsonResponse(exchange, 201, Map.of(
                        "success", true,
                        "message", "Application submitted successfully.",
                        "application", app
                ));
            } else {
                sendJsonResponse(exchange, 405, Map.of("error", "Method not allowed."));
            }
        } else {
            // /api/applications/{id}
            int appId = Integer.parseInt(subPath.startsWith("/") ? subPath.substring(1) : subPath);
            if ("PUT".equals(method)) {
                if (!"EMPLOYER".equalsIgnoreCase(currentUser.getRole()) && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                    sendJsonResponse(exchange, 403, Map.of("error", "Only employers and admins can update application statuses."));
                    return;
                }
                Map<String, Object> body = readJsonBody(exchange);
                String status = JsonUtil.getString(body, "status", "UNDER_REVIEW");
                boolean ok = applicationDAO.updateStatus(appId, status);
                sendJsonResponse(exchange, 200, Map.of("success", ok, "status", status));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Saved Opportunities Handlers
    // -------------------------------------------------------------------------
    private void handleSavedOpportunities(HttpExchange exchange, String path, String method,
                                          Map<String, String> qParams, User currentUser) throws IOException {
        if (currentUser == null || !"STUDENT".equalsIgnoreCase(currentUser.getRole())) {
            sendJsonResponse(exchange, 401, Map.of("error", "Authentication as student required."));
            return;
        }

        StudentProfile sp = studentDAO.findByUserId(currentUser.getId());
        if (sp == null) {
            sendJsonResponse(exchange, 200, Map.of("saved", Collections.emptyList()));
            return;
        }

        String sub = path.substring("/api/saved-opportunities".length());

        if (sub.isEmpty() || "/".equals(sub)) {
            if ("GET".equals(method)) {
                List<Opportunity> saved = opportunityDAO.findSavedByStudent(sp.getId());
                sendJsonResponse(exchange, 200, Map.of("saved", saved));
            } else if ("POST".equals(method)) {
                Map<String, Object> body = readJsonBody(exchange);
                int oppId = JsonUtil.getInt(body, "opportunityId", 0);
                boolean ok = opportunityDAO.saveOpportunity(sp.getId(), oppId);
                sendJsonResponse(exchange, 200, Map.of("success", ok, "message", "Opportunity bookmarked."));
            }
        } else {
            int oppId = Integer.parseInt(sub.startsWith("/") ? sub.substring(1) : sub);
            if ("DELETE".equals(method)) {
                boolean ok = opportunityDAO.unsaveOpportunity(sp.getId(), oppId);
                sendJsonResponse(exchange, 200, Map.of("success", ok, "message", "Opportunity removed from saved list."));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Employer Handlers (Candidate Search & Stats)
    // -------------------------------------------------------------------------
    private void handleEmployer(HttpExchange exchange, String path, String method,
                                Map<String, String> qParams, User currentUser) throws IOException {
        if (currentUser == null || (!"EMPLOYER".equalsIgnoreCase(currentUser.getRole()) && !"ADMIN".equalsIgnoreCase(currentUser.getRole()))) {
            sendJsonResponse(exchange, 403, Map.of("error", "Employer authorization required."));
            return;
        }

        String sub = path.substring("/api/employer/".length());

        if ("candidates".equals(sub) && "GET".equals(method)) {
            // STRICT PRIVACY COMPLIANCE:
            // Only profiles where consent_given = true AND profile_visibility = true are returned!
            String skill = qParams.get("skill");
            String branch = qParams.get("branch");
            String edu = qParams.get("education");
            String loc = qParams.get("location");
            String cat = qParams.get("category");

            List<StudentProfile> candidates = studentDAO.searchCandidates(skill, branch, edu, loc, cat);
            sendJsonResponse(exchange, 200, Map.of("candidates", candidates, "count", candidates.size()));
        } else if ("stats".equals(sub) && "GET".equals(method)) {
            EmployerProfile ep = employerDAO.findByUserId(currentUser.getId());
            if (ep == null) {
                sendJsonResponse(exchange, 200, Map.of("activeOpportunities", 0, "totalApplications", 0, "shortlisted", 0, "hired", 0));
                return;
            }
            List<Opportunity> opps = opportunityDAO.findByEmployerId(ep.getId());
            List<Application> apps = applicationDAO.findByEmployerId(ep.getId());

            int shortlisted = 0;
            int hired = 0;
            for (Application a : apps) {
                if ("SHORTLISTED".equalsIgnoreCase(a.getStatus())) shortlisted++;
                if ("SELECTED".equalsIgnoreCase(a.getStatus())) hired++;
            }

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("activeOpportunities", opps.size());
            stats.put("totalApplications", apps.size());
            stats.put("shortlisted", shortlisted);
            stats.put("hired", hired);
            stats.put("verificationStatus", ep.getVerificationStatus());
            sendJsonResponse(exchange, 200, stats);
        } else if ("opportunities".equals(sub) && "GET".equals(method)) {
            EmployerProfile ep = employerDAO.findByUserId(currentUser.getId());
            if (ep == null) {
                sendJsonResponse(exchange, 200, Map.of("opportunities", Collections.emptyList()));
                return;
            }
            List<Opportunity> opps = opportunityDAO.findByEmployerId(ep.getId());
            sendJsonResponse(exchange, 200, Map.of("opportunities", opps));
        } else {
            sendJsonResponse(exchange, 404, Map.of("error", "Employer route not found."));
        }
    }

    // -------------------------------------------------------------------------
    // Trusted Execution Environment status
    // -------------------------------------------------------------------------
    private void handleTeeStatus(HttpExchange exchange, String method) throws IOException {
        if ("GET".equals(method)) {
            sendJsonResponse(exchange, 200, TrustedExecutionEnvironment.getStatus());
        } else {
            sendJsonResponse(exchange, 405, Map.of("error", "Method not allowed."));
        }
    }

    // -------------------------------------------------------------------------
    // Eligibility & Government Programs Handlers
    // -------------------------------------------------------------------------
    private void handleEligibility(HttpExchange exchange, String method, Map<String, String> qParams) throws IOException {
        if ("GET".equals(method)) {
            String state = qParams.get("state");
            String category = qParams.get("category");
            String education = qParams.get("education");
            String branch = qParams.get("branch");
            String skills = qParams.get("skills");

            Map<String, List<EligibilityService.ProgramMatch>> eval =
                    eligibilityService.evaluateEligibility(state, category, education, branch, skills);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("results", eval);
            resp.put("disclaimer", "Always verify final eligibility, domicile conditions, and annual quotas on the official government department website. This assessment is a platform recommendation.");
            sendJsonResponse(exchange, 200, resp);
        } else {
            sendJsonResponse(exchange, 405, Map.of("error", "Method not allowed."));
        }
    }

    private void handleGovernmentPrograms(HttpExchange exchange, String method, Map<String, String> qParams) throws IOException {
        if ("GET".equals(method)) {
            String type = qParams.get("type");
            List<GovernmentProgram> programs = programDAO.findAll(type);
            sendJsonResponse(exchange, 200, Map.of("programs", programs, "count", programs.size()));
        } else {
            sendJsonResponse(exchange, 405, Map.of("error", "Method not allowed."));
        }
    }

    // -------------------------------------------------------------------------
    // Notifications Handlers
    // -------------------------------------------------------------------------
    private void handleNotifications(HttpExchange exchange, String path, String method, User currentUser) throws IOException {
        if (currentUser == null) {
            sendJsonResponse(exchange, 401, Map.of("error", "Authentication required."));
            return;
        }

        String sub = path.substring("/api/notifications".length());

        if (sub.isEmpty() || "/".equals(sub)) {
            if ("GET".equals(method)) {
                List<Notification> list = notificationDAO.findByUserId(currentUser.getId());
                int unread = 0;
                for (Notification n : list) if (!n.isRead()) unread++;
                sendJsonResponse(exchange, 200, Map.of("notifications", list, "unreadCount", unread));
            }
        } else if ("/read-all".equals(sub) && "PUT".equals(method)) {
            boolean ok = notificationDAO.markAllAsRead(currentUser.getId());
            sendJsonResponse(exchange, 200, Map.of("success", ok));
        } else {
            int notifId = Integer.parseInt(sub.startsWith("/") ? sub.substring(1) : sub);
            if ("PUT".equals(method)) {
                boolean ok = notificationDAO.markAsRead(notifId, currentUser.getId());
                sendJsonResponse(exchange, 200, Map.of("success", ok));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Admin Handlers
    // -------------------------------------------------------------------------
    private void handleAdmin(HttpExchange exchange, String path, String method, User currentUser) throws IOException {
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            sendJsonResponse(exchange, 403, Map.of("error", "Administrative privileges required."));
            return;
        }

        String sub = path.substring("/api/admin/".length());

        if ("users".equals(sub) && "GET".equals(method)) {
            List<User> users = userDAO.findAll();
            sendJsonResponse(exchange, 200, Map.of("users", users));
        } else if ("employers".equals(sub) && "GET".equals(method)) {
            List<EmployerProfile> employers = employerDAO.findAll();
            sendJsonResponse(exchange, 200, Map.of("employers", employers));
        } else if (sub.startsWith("employers/") && sub.endsWith("/verify") && "PUT".equals(method)) {
            String idStr = sub.substring("employers/".length(), sub.indexOf("/verify"));
            int empId = Integer.parseInt(idStr);
            Map<String, Object> body = readJsonBody(exchange);
            String status = JsonUtil.getString(body, "status", "VERIFIED");
            boolean ok = employerDAO.updateVerificationStatus(empId, status);
            sendJsonResponse(exchange, 200, Map.of("success", ok, "status", status));
        } else if (sub.startsWith("opportunities/") && "DELETE".equals(method)) {
            int oppId = Integer.parseInt(sub.substring("opportunities/".length()));
            boolean ok = opportunityDAO.delete(oppId);
            sendJsonResponse(exchange, 200, Map.of("success", ok));
        } else {
            sendJsonResponse(exchange, 404, Map.of("error", "Admin endpoint not found."));
        }
    }

    // -------------------------------------------------------------------------
    // HTTP Helpers
    // -------------------------------------------------------------------------
    private User getAuthenticatedUser(HttpExchange exchange) {
        String token = getBearerToken(exchange);
        if (token != null) {
            return authService.getUserByToken(token);
        }
        return null;
    }

    private String getBearerToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }

    private Map<String, Object> readJsonBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            String raw = baos.toString(StandardCharsets.UTF_8);
            return JsonUtil.parseJsonObject(raw);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                try {
                    map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = JsonUtil.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
