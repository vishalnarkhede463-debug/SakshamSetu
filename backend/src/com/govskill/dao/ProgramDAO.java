package com.govskill.dao;

import com.govskill.models.GovernmentProgram;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramDAO {

    public List<GovernmentProgram> findAll(String type) {
        List<GovernmentProgram> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM government_programs ";
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
                sql += "WHERE type = ? ";
            }
            sql += "ORDER BY id ASC";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
                    ps.setString(1, type.trim().toUpperCase());
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapProgram(rs));
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (GovernmentProgram gp : DBConnection.store.governmentPrograms.values()) {
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
                if (!type.equalsIgnoreCase(gp.getType())) continue;
            }
            list.add(gp);
        }
        return list;
    }

    public GovernmentProgram findById(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM government_programs WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapProgram(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DBConnection.store.governmentPrograms.get(id);
    }

    public GovernmentProgram create(GovernmentProgram p) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO government_programs (name, department, type, description, eligibility, benefits, application_url, deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getName());
                ps.setString(2, p.getDepartment());
                ps.setString(3, p.getType());
                ps.setString(4, p.getDescription());
                ps.setString(5, p.getEligibility());
                ps.setString(6, p.getBenefits());
                ps.setString(7, p.getApplicationUrl());
                ps.setString(8, p.getDeadline());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
                return p;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.progIdSeq.incrementAndGet();
        p.setId(newId);
        DBConnection.store.governmentPrograms.put(newId, p);
        return p;
    }

    public boolean delete(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "DELETE FROM government_programs WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DBConnection.store.governmentPrograms.remove(id) != null;
    }

    private GovernmentProgram mapProgram(ResultSet rs) throws SQLException {
        return new GovernmentProgram(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("department"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("eligibility"),
                rs.getString("benefits"),
                rs.getString("application_url"),
                rs.getString("deadline")
        );
    }
}
