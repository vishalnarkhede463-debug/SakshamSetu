package com.govskill.dao;

import com.govskill.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) {
        if (email == null) return null;
        email = email.trim().toLowerCase();

        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM users WHERE LOWER(email) = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapUser(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Fallback
        for (User u : DBConnection.store.users.values()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public User findById(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapUser(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DBConnection.store.users.get(id);
    }

    public User create(User user) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO users (name, email, phone, password_hash, salt, role, state, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail().toLowerCase());
                ps.setString(3, user.getPhone());
                ps.setString(4, user.getPasswordHash());
                ps.setString(5, user.getSalt());
                ps.setString(6, user.getRole());
                ps.setString(7, user.getState());
                ps.setString(8, user.getCity());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }
                return user;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.userIdSeq.incrementAndGet();
        user.setId(newId);
        DBConnection.store.users.put(newId, user);
        return user;
    }

    public List<User> findAll() {
        if (DBConnection.isMySqlAvailable()) {
            List<User> list = new ArrayList<>();
            String sql = "SELECT * FROM users ORDER BY id DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapUser(rs));
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(DBConnection.store.users.values());
    }

    public boolean delete(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "DELETE FROM users WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DBConnection.store.users.remove(id) != null;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setSalt(rs.getString("salt"));
        u.setRole(rs.getString("role"));
        u.setState(rs.getString("state"));
        u.setCity(rs.getString("city"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
