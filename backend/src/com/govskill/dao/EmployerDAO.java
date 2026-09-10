package com.govskill.dao;

import com.govskill.models.EmployerProfile;
import com.govskill.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployerDAO {

    public EmployerProfile findByUserId(int userId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT ep.*, u.email, u.phone, u.state, u.city FROM employer_profiles ep " +
                         "JOIN users u ON ep.user_id = u.id WHERE ep.user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapEmployer(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (EmployerProfile ep : DBConnection.store.employerProfiles.values()) {
            if (ep.getUserId() == userId) {
                populateUserDetails(ep);
                return ep;
            }
        }
        return null;
    }

    public EmployerProfile findById(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT ep.*, u.email, u.phone, u.state, u.city FROM employer_profiles ep " +
                         "JOIN users u ON ep.user_id = u.id WHERE ep.id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapEmployer(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        EmployerProfile ep = DBConnection.store.employerProfiles.get(id);
        if (ep != null) populateUserDetails(ep);
        return ep;
    }

    public EmployerProfile create(EmployerProfile ep) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO employer_profiles (user_id, organization_name, organization_type, description, website, verification_status) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, ep.getUserId());
                ps.setString(2, ep.getOrganizationName());
                ps.setString(3, ep.getOrganizationType());
                ps.setString(4, ep.getDescription());
                ps.setString(5, ep.getWebsite());
                ps.setString(6, ep.getVerificationStatus() != null ? ep.getVerificationStatus() : "PENDING");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) ep.setId(rs.getInt(1));
                }
                return ep;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.empIdSeq.incrementAndGet();
        ep.setId(newId);
        if (ep.getVerificationStatus() == null) ep.setVerificationStatus("PENDING");
        populateUserDetails(ep);
        DBConnection.store.employerProfiles.put(newId, ep);
        return ep;
    }

    public boolean update(EmployerProfile ep) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE employer_profiles SET organization_name = ?, organization_type = ?, description = ?, website = ?, verification_status = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ep.getOrganizationName());
                ps.setString(2, ep.getOrganizationType());
                ps.setString(3, ep.getDescription());
                ps.setString(4, ep.getWebsite());
                ps.setString(5, ep.getVerificationStatus());
                ps.setInt(6, ep.getId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        DBConnection.store.employerProfiles.put(ep.getId(), ep);
        return true;
    }

    public List<EmployerProfile> findAll() {
        List<EmployerProfile> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT ep.*, u.email, u.phone, u.state, u.city FROM employer_profiles ep " +
                         "JOIN users u ON ep.user_id = u.id ORDER BY ep.id DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEmployer(rs));
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (EmployerProfile ep : DBConnection.store.employerProfiles.values()) {
            populateUserDetails(ep);
            list.add(ep);
        }
        return list;
    }

    public boolean updateVerificationStatus(int id, String status) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE employer_profiles SET verification_status = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        EmployerProfile ep = DBConnection.store.employerProfiles.get(id);
        if (ep != null) {
            ep.setVerificationStatus(status);
            return true;
        }
        return false;
    }

    private void populateUserDetails(EmployerProfile ep) {
        User u = DBConnection.store.users.get(ep.getUserId());
        if (u != null) {
            ep.setEmail(u.getEmail());
            ep.setPhone(u.getPhone());
            ep.setState(u.getState());
            ep.setCity(u.getCity());
        }
    }

    private EmployerProfile mapEmployer(ResultSet rs) throws SQLException {
        EmployerProfile ep = new EmployerProfile();
        ep.setId(rs.getInt("id"));
        ep.setUserId(rs.getInt("user_id"));
        ep.setOrganizationName(rs.getString("organization_name"));
        ep.setOrganizationType(rs.getString("organization_type"));
        ep.setDescription(rs.getString("description"));
        ep.setWebsite(rs.getString("website"));
        ep.setVerificationStatus(rs.getString("verification_status"));
        ep.setEmail(rs.getString("email"));
        ep.setPhone(rs.getString("phone"));
        ep.setState(rs.getString("state"));
        ep.setCity(rs.getString("city"));
        return ep;
    }
}
