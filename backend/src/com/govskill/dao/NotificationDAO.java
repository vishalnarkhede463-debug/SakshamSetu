package com.govskill.dao;

import com.govskill.models.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<Notification> findByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapNotification(rs));
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Notification n : DBConnection.store.notifications.values()) {
            if (n.getUserId() == userId) {
                list.add(n);
            }
        }
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return list;
    }

    public Notification create(Notification notif) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO notifications (user_id, title, message, is_read) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, notif.getUserId());
                ps.setString(2, notif.getTitle());
                ps.setString(3, notif.getMessage());
                ps.setBoolean(4, notif.isRead());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) notif.setId(rs.getInt(1));
                }
                return notif;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.notifIdSeq.incrementAndGet();
        notif.setId(newId);
        if (notif.getCreatedAt() == null) notif.setCreatedAt("2026-09-08 09:30");
        DBConnection.store.notifications.put(newId, notif);
        return notif;
    }

    public boolean markAsRead(int notificationId, int userId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ? AND user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, notificationId);
                ps.setInt(2, userId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Notification n = DBConnection.store.notifications.get(notificationId);
        if (n != null && n.getUserId() == userId) {
            n.setRead(true);
            return true;
        }
        return false;
    }

    public boolean markAllAsRead(int userId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Notification n : DBConnection.store.notifications.values()) {
            if (n.getUserId() == userId) {
                n.setRead(true);
            }
        }
        return true;
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getString("created_at"));
        return n;
    }
}
