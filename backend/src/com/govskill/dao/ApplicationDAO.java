package com.govskill.dao;

import com.govskill.models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public boolean hasApplied(int studentId, int opportunityId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT 1 FROM applications WHERE student_id = ? AND opportunity_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, opportunityId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Application a : DBConnection.store.applications.values()) {
            if (a.getStudentId() == studentId && a.getOpportunityId() == opportunityId) {
                return true;
            }
        }
        return false;
    }

    public Application create(Application app) {
        if (hasApplied(app.getStudentId(), app.getOpportunityId())) {
            return null; // Duplicate prevention
        }

        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO applications (opportunity_id, student_id, status, cover_note) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, app.getOpportunityId());
                ps.setInt(2, app.getStudentId());
                ps.setString(3, app.getStatus() != null ? app.getStatus() : "APPLIED");
                ps.setString(4, app.getCoverNote());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) app.setId(rs.getInt(1));
                }

                // Send notification to student
                StudentProfile sp = new StudentDAO().findById(app.getStudentId());
                Opportunity opp = new OpportunityDAO().findById(app.getOpportunityId());
                if (sp != null && opp != null) {
                    new NotificationDAO().create(new Notification(0, sp.getUserId(),
                            "Application Submitted",
                            "Your application for \"" + opp.getTitle() + "\" has been submitted successfully.",
                            false, null));
                }
                return app;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.appIdSeq.incrementAndGet();
        app.setId(newId);
        if (app.getStatus() == null) app.setStatus("APPLIED");
        app.setAppliedAt("2026-09-08");
        app.setUpdatedAt("2026-09-08");
        DBConnection.store.applications.put(newId, app);

        // Notify student
        StudentProfile sp = DBConnection.store.studentProfiles.get(app.getStudentId());
        Opportunity opp = DBConnection.store.opportunities.get(app.getOpportunityId());
        if (sp != null && opp != null) {
            new NotificationDAO().create(new Notification(0, sp.getUserId(),
                    "Application Submitted",
                    "Your application for \"" + opp.getTitle() + "\" has been submitted successfully.",
                    false, "2026-09-08 09:00"));
        }

        return app;
    }

    public List<Application> findByStudentId(int studentId) {
        List<Application> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT a.*, o.title, o.organization, o.type, o.location, o.stipend_or_salary, o.deadline " +
                         "FROM applications a JOIN opportunities o ON a.opportunity_id = o.id " +
                         "WHERE a.student_id = ? ORDER BY a.applied_at DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Application a = mapApplication(rs);
                        a.setOpportunityTitle(rs.getString("title"));
                        a.setOrganization(rs.getString("organization"));
                        a.setOpportunityType(rs.getString("type"));
                        a.setLocation(rs.getString("location"));
                        a.setStipendOrSalary(rs.getString("stipend_or_salary"));
                        a.setDeadline(rs.getString("deadline"));
                        list.add(a);
                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Application a : DBConnection.store.applications.values()) {
            if (a.getStudentId() == studentId) {
                Opportunity o = DBConnection.store.opportunities.get(a.getOpportunityId());
                if (o != null) {
                    a.setOpportunityTitle(o.getTitle());
                    a.setOrganization(o.getOrganization());
                    a.setOpportunityType(o.getType());
                    a.setLocation(o.getLocation());
                    a.setStipendOrSalary(o.getStipendOrSalary());
                    a.setDeadline(o.getDeadline());
                }
                list.add(a);
            }
        }
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return list;
    }

    public List<Application> findByEmployerId(int employerId) {
        List<Application> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT a.*, o.title, o.organization, u.name as stu_name, u.email as stu_email, u.phone as stu_phone, " +
                         "u.city as stu_city, sp.branch as stu_branch, sp.education_level as stu_edu, sp.college as stu_college " +
                         "FROM applications a " +
                         "JOIN opportunities o ON a.opportunity_id = o.id " +
                         "JOIN student_profiles sp ON a.student_id = sp.id " +
                         "JOIN users u ON sp.user_id = u.id " +
                         "WHERE o.employer_id = ? ORDER BY a.applied_at DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, employerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Application a = mapApplication(rs);
                        a.setOpportunityTitle(rs.getString("title"));
                        a.setOrganization(rs.getString("organization"));
                        a.setStudentName(rs.getString("stu_name"));
                        a.setStudentEmail(rs.getString("stu_email"));
                        a.setPhone(rs.getString("stu_phone"));
                        a.setStudentCity(rs.getString("stu_city"));
                        a.setStudentBranch(rs.getString("stu_branch"));
                        a.setStudentEducationLevel(rs.getString("stu_edu"));
                        a.setStudentCollege(rs.getString("stu_college"));
                        list.add(a);
                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Application a : DBConnection.store.applications.values()) {
            Opportunity o = DBConnection.store.opportunities.get(a.getOpportunityId());
            if (o != null && o.getEmployerId() != null && o.getEmployerId() == employerId) {
                a.setOpportunityTitle(o.getTitle());
                a.setOrganization(o.getOrganization());

                StudentProfile sp = DBConnection.store.studentProfiles.get(a.getStudentId());
                if (sp != null) {
                    a.setStudentName(sp.getName());
                    a.setStudentEmail(sp.getEmail());
                    a.setPhone(sp.getPhone());
                    a.setStudentCity(sp.getCity());
                    a.setStudentBranch(sp.getBranch());
                    a.setStudentEducationLevel(sp.getEducationLevel());
                    a.setStudentCollege(sp.getCollege());
                }
                list.add(a);
            }
        }
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return list;
    }

    public boolean updateStatus(int applicationId, String status) {
        Application a = null;
        Opportunity opp = null;
        StudentProfile sp = null;

        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE applications SET status = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, applicationId);
                boolean ok = ps.executeUpdate() > 0;
                if (ok) {
                    // Fetch for notification
                    String findSql = "SELECT a.student_id, a.opportunity_id, sp.user_id, o.title " +
                                    "FROM applications a JOIN student_profiles sp ON a.student_id = sp.id " +
                                    "JOIN opportunities o ON a.opportunity_id = o.id WHERE a.id = ?";
                    try (PreparedStatement fps = conn.prepareStatement(findSql)) {
                        fps.setInt(1, applicationId);
                        try (ResultSet rs = fps.executeQuery()) {
                            if (rs.next()) {
                                int userId = rs.getInt("user_id");
                                String oppTitle = rs.getString("title");
                                new NotificationDAO().create(new Notification(0, userId,
                                        "Application Status Updated",
                                        "Your application for \"" + oppTitle + "\" has been updated to: " + status.replace("_", " "),
                                        false, null));
                            }
                        }
                    }
                }
                return ok;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        a = DBConnection.store.applications.get(applicationId);
        if (a != null) {
            a.setStatus(status);
            a.setUpdatedAt("2026-09-08");

            opp = DBConnection.store.opportunities.get(a.getOpportunityId());
            sp = DBConnection.store.studentProfiles.get(a.getStudentId());
            if (opp != null && sp != null) {
                new NotificationDAO().create(new Notification(0, sp.getUserId(),
                        "Application Status Updated",
                        "Your application for \"" + opp.getTitle() + "\" has been updated to: " + status.replace("_", " "),
                        false, "2026-09-08 11:30"));
            }
            return true;
        }
        return false;
    }

    public List<Application> findAll() {
        return new ArrayList<>(DBConnection.store.applications.values());
    }

    private Application mapApplication(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setOpportunityId(rs.getInt("opportunity_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setStatus(rs.getString("status"));
        a.setCoverNote(rs.getString("cover_note"));
        a.setAppliedAt(rs.getString("applied_at"));
        a.setUpdatedAt(rs.getString("updated_at"));
        return a;
    }
}
