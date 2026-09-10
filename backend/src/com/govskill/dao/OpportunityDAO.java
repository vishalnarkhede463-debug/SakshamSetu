package com.govskill.dao;

import com.govskill.models.Opportunity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OpportunityDAO {

    public List<Opportunity> search(String query, String type, String state, String city,
                                    String branch, String education, String skill, String category) {
        List<Opportunity> list = new ArrayList<>();

        if (DBConnection.isMySqlAvailable()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM opportunities WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (query != null && !query.trim().isEmpty()) {
                sql.append("AND (LOWER(title) LIKE ? OR LOWER(organization) LIKE ? OR LOWER(description) LIKE ? OR LOWER(skills) LIKE ?) ");
                String q = "%" + query.trim().toLowerCase() + "%";
                params.add(q); params.add(q); params.add(q); params.add(q);
            }
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
                sql.append("AND type = ? ");
                params.add(type.trim().toUpperCase());
            }
            if (state != null && !state.trim().isEmpty() && !"ALL".equalsIgnoreCase(state)) {
                sql.append("AND LOWER(state) = ? ");
                params.add(state.trim().toLowerCase());
            }
            if (city != null && !city.trim().isEmpty() && !"ALL".equalsIgnoreCase(city)) {
                sql.append("AND LOWER(city) = ? ");
                params.add(city.trim().toLowerCase());
            }
            if (branch != null && !branch.trim().isEmpty() && !"ALL".equalsIgnoreCase(branch)) {
                sql.append("AND (LOWER(branch) LIKE ? OR LOWER(branch) = 'all' OR LOWER(branch) = 'any') ");
                params.add("%" + branch.trim().toLowerCase() + "%");
            }
            if (education != null && !education.trim().isEmpty() && !"ALL".equalsIgnoreCase(education)) {
                sql.append("AND (LOWER(education_requirement) LIKE ? OR LOWER(education_requirement) = 'any') ");
                params.add("%" + education.trim().toLowerCase() + "%");
            }
            if (skill != null && !skill.trim().isEmpty()) {
                sql.append("AND LOWER(skills) LIKE ? ");
                params.add("%" + skill.trim().toLowerCase() + "%");
            }
            if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
                sql.append("AND (category = ? OR category = 'ALL') ");
                params.add(category.trim().toUpperCase());
            }

            sql.append("ORDER BY id DESC");

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapOpportunity(rs));
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Fallback filtering in memory
        for (Opportunity o : DBConnection.store.opportunities.values()) {
            if (query != null && !query.trim().isEmpty()) {
                String q = query.trim().toLowerCase();
                boolean match = (o.getTitle() != null && o.getTitle().toLowerCase().contains(q)) ||
                                (o.getOrganization() != null && o.getOrganization().toLowerCase().contains(q)) ||
                                (o.getDescription() != null && o.getDescription().toLowerCase().contains(q)) ||
                                (o.getSkills() != null && o.getSkills().toLowerCase().contains(q)) ||
                                (o.getLocation() != null && o.getLocation().toLowerCase().contains(q));
                if (!match) continue;
            }
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
                if (!type.equalsIgnoreCase(o.getType())) continue;
            }
            if (state != null && !state.trim().isEmpty() && !"ALL".equalsIgnoreCase(state)) {
                if (!state.equalsIgnoreCase(o.getState())) continue;
            }
            if (city != null && !city.trim().isEmpty() && !"ALL".equalsIgnoreCase(city)) {
                if (!city.equalsIgnoreCase(o.getCity())) continue;
            }
            if (branch != null && !branch.trim().isEmpty() && !"ALL".equalsIgnoreCase(branch)) {
                if (o.getBranch() != null && !o.getBranch().toLowerCase().contains(branch.trim().toLowerCase()) && !o.getBranch().equalsIgnoreCase("all") && !o.getBranch().equalsIgnoreCase("any")) continue;
            }
            if (education != null && !education.trim().isEmpty() && !"ALL".equalsIgnoreCase(education)) {
                if (o.getEducationRequirement() != null && !o.getEducationRequirement().toLowerCase().contains(education.trim().toLowerCase()) && !o.getEducationRequirement().equalsIgnoreCase("any")) continue;
            }
            if (skill != null && !skill.trim().isEmpty()) {
                if (o.getSkills() == null || !o.getSkills().toLowerCase().contains(skill.trim().toLowerCase())) continue;
            }
            if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
                if (!category.equalsIgnoreCase(o.getCategory()) && !"ALL".equalsIgnoreCase(o.getCategory())) continue;
            }

            list.add(o);
        }

        // Sort descending by id
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return list;
    }

    public Opportunity findById(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM opportunities WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapOpportunity(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DBConnection.store.opportunities.get(id);
    }

    public Opportunity create(Opportunity opp) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO opportunities (employer_id, title, description, type, organization, location, state, city, branch, education_requirement, skills, category, stipend_or_salary, deadline, source_url, verified) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (opp.getEmployerId() != null) ps.setInt(1, opp.getEmployerId()); else ps.setNull(1, Types.INTEGER);
                ps.setString(2, opp.getTitle());
                ps.setString(3, opp.getDescription());
                ps.setString(4, opp.getType());
                ps.setString(5, opp.getOrganization());
                ps.setString(6, opp.getLocation());
                ps.setString(7, opp.getState());
                ps.setString(8, opp.getCity());
                ps.setString(9, opp.getBranch());
                ps.setString(10, opp.getEducationRequirement());
                ps.setString(11, opp.getSkills());
                ps.setString(12, opp.getCategory() != null ? opp.getCategory() : "ALL");
                ps.setString(13, opp.getStipendOrSalary());
                ps.setString(14, opp.getDeadline());
                ps.setString(15, opp.getSourceUrl());
                ps.setBoolean(16, opp.isVerified());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) opp.setId(rs.getInt(1));
                }
                return opp;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.oppIdSeq.incrementAndGet();
        opp.setId(newId);
        opp.setCreatedAt("2026-09-08");
        DBConnection.store.opportunities.put(newId, opp);
        return opp;
    }

    public boolean update(Opportunity opp) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE opportunities SET title = ?, description = ?, type = ?, organization = ?, location = ?, state = ?, city = ?, " +
                         "branch = ?, education_requirement = ?, skills = ?, category = ?, stipend_or_salary = ?, deadline = ?, source_url = ?, verified = ? " +
                         "WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, opp.getTitle());
                ps.setString(2, opp.getDescription());
                ps.setString(3, opp.getType());
                ps.setString(4, opp.getOrganization());
                ps.setString(5, opp.getLocation());
                ps.setString(6, opp.getState());
                ps.setString(7, opp.getCity());
                ps.setString(8, opp.getBranch());
                ps.setString(9, opp.getEducationRequirement());
                ps.setString(10, opp.getSkills());
                ps.setString(11, opp.getCategory());
                ps.setString(12, opp.getStipendOrSalary());
                ps.setString(13, opp.getDeadline());
                ps.setString(14, opp.getSourceUrl());
                ps.setBoolean(15, opp.isVerified());
                ps.setInt(16, opp.getId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        DBConnection.store.opportunities.put(opp.getId(), opp);
        return true;
    }

    public boolean delete(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "DELETE FROM opportunities WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DBConnection.store.opportunities.remove(id) != null;
    }

    public List<Opportunity> findByEmployerId(int employerId) {
        List<Opportunity> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM opportunities WHERE employer_id = ? ORDER BY id DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, employerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapOpportunity(rs));
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Opportunity o : DBConnection.store.opportunities.values()) {
            if (o.getEmployerId() != null && o.getEmployerId() == employerId) {
                list.add(o);
            }
        }
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return list;
    }

    public boolean saveOpportunity(int studentId, int opportunityId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT IGNORE INTO saved_opportunities (opportunity_id, student_id) VALUES (?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, opportunityId);
                ps.setInt(2, studentId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return DBConnection.store.savedOpportunities.add(studentId + "_" + opportunityId);
    }

    public boolean unsaveOpportunity(int studentId, int opportunityId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "DELETE FROM saved_opportunities WHERE opportunity_id = ? AND student_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, opportunityId);
                ps.setInt(2, studentId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return DBConnection.store.savedOpportunities.remove(studentId + "_" + opportunityId);
    }

    public boolean isOpportunitySaved(int studentId, int opportunityId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT 1 FROM saved_opportunities WHERE opportunity_id = ? AND student_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, opportunityId);
                ps.setInt(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return DBConnection.store.savedOpportunities.contains(studentId + "_" + opportunityId);
    }

    public List<Opportunity> findSavedByStudent(int studentId) {
        List<Opportunity> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT o.* FROM opportunities o JOIN saved_opportunities so ON o.id = so.opportunity_id WHERE so.student_id = ? ORDER BY so.created_at DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapOpportunity(rs));
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (String pair : DBConnection.store.savedOpportunities) {
            String[] parts = pair.split("_");
            if (parts.length == 2 && Integer.parseInt(parts[0]) == studentId) {
                int oppId = Integer.parseInt(parts[1]);
                Opportunity o = DBConnection.store.opportunities.get(oppId);
                if (o != null) list.add(o);
            }
        }
        return list;
    }

    private Opportunity mapOpportunity(ResultSet rs) throws SQLException {
        Opportunity o = new Opportunity();
        o.setId(rs.getInt("id"));
        int empId = rs.getInt("employer_id");
        if (!rs.wasNull()) o.setEmployerId(empId);
        o.setTitle(rs.getString("title"));
        o.setDescription(rs.getString("description"));
        o.setType(rs.getString("type"));
        o.setOrganization(rs.getString("organization"));
        o.setLocation(rs.getString("location"));
        o.setState(rs.getString("state"));
        o.setCity(rs.getString("city"));
        o.setBranch(rs.getString("branch"));
        o.setEducationRequirement(rs.getString("education_requirement"));
        o.setSkills(rs.getString("skills"));
        o.setCategory(rs.getString("category"));
        o.setStipendOrSalary(rs.getString("stipend_or_salary"));
        o.setDeadline(rs.getString("deadline"));
        o.setSourceUrl(rs.getString("source_url"));
        o.setVerified(rs.getBoolean("verified"));
        o.setCreatedAt(rs.getString("created_at"));
        return o;
    }
}
