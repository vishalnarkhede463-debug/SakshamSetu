package com.govskill.dao;

import com.govskill.models.Certificate;
import com.govskill.models.Skill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificateDAO {

    public List<Certificate> findByStudentProfileId(int studentProfileId) {
        List<Certificate> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT c.*, s.name as skill_name FROM certificates c " +
                         "LEFT JOIN skills s ON c.skill_id = s.id " +
                         "WHERE c.student_profile_id = ? ORDER BY c.uploaded_at DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentProfileId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapCertificate(rs));
                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Certificate c : DBConnection.store.certificates.values()) {
            if (c.getStudentProfileId() == studentProfileId) {
                if (c.getSkillId() != null) {
                    Skill s = DBConnection.store.skills.get(c.getSkillId());
                    if (s != null) c.setSkillName(s.getName());
                }
                list.add(c);
            }
        }
        return list;
    }

    public Certificate create(Certificate cert) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO certificates (student_profile_id, skill_id, name, issuing_organization, certificate_url, issue_date) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cert.getStudentProfileId());
                if (cert.getSkillId() != null) ps.setInt(2, cert.getSkillId()); else ps.setNull(2, Types.INTEGER);
                ps.setString(3, cert.getName());
                ps.setString(4, cert.getIssuingOrganization());
                ps.setString(5, cert.getCertificateUrl());
                ps.setString(6, cert.getIssueDate());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) cert.setId(rs.getInt(1));
                }

                // Verify the skill now that certificate is uploaded!
                if (cert.getSkillId() != null) {
                    new StudentDAO().updateSkillVerification(cert.getSkillId(), true);
                }
                return cert;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.certIdSeq.incrementAndGet();
        cert.setId(newId);
        if (cert.getSkillId() != null) {
            Skill s = DBConnection.store.skills.get(cert.getSkillId());
            if (s != null) {
                cert.setSkillName(s.getName());
                s.setVerified(true); // Rule: skill becomes verified after uploading certificate!
            }
        }
        DBConnection.store.certificates.put(newId, cert);
        return cert;
    }

    public boolean delete(int id, int studentProfileId) {
        Certificate target = null;

        if (DBConnection.isMySqlAvailable()) {
            String findSql = "SELECT skill_id FROM certificates WHERE id = ? AND student_profile_id = ?";
            Integer skillId = null;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, id);
                ps.setInt(2, studentProfileId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int sid = rs.getInt("skill_id");
                        if (!rs.wasNull()) skillId = sid;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String deleteSql = "DELETE FROM certificates WHERE id = ? AND student_profile_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, id);
                ps.setInt(2, studentProfileId);
                boolean deleted = ps.executeUpdate() > 0;

                // If skillId existed, check if any remaining certificate covers it
                if (deleted && skillId != null) {
                    String checkSql = "SELECT COUNT(*) FROM certificates WHERE skill_id = ?";
                    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                        checkPs.setInt(1, skillId);
                        try (ResultSet rs = checkPs.executeQuery()) {
                            if (rs.next() && rs.getInt(1) == 0) {
                                new StudentDAO().updateSkillVerification(skillId, false);
                            }
                        }
                    }
                }
                return deleted;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        target = DBConnection.store.certificates.get(id);
        if (target != null && target.getStudentProfileId() == studentProfileId) {
            DBConnection.store.certificates.remove(id);
            if (target.getSkillId() != null) {
                // Check if any other certificate exists for this skill
                boolean stillCovered = false;
                for (Certificate c : DBConnection.store.certificates.values()) {
                    if (c.getSkillId() != null && c.getSkillId().equals(target.getSkillId())) {
                        stillCovered = true;
                        break;
                    }
                }
                if (!stillCovered) {
                    Skill s = DBConnection.store.skills.get(target.getSkillId());
                    if (s != null) s.setVerified(false);
                }
            }
            return true;
        }
        return false;
    }

    private Certificate mapCertificate(ResultSet rs) throws SQLException {
        Certificate c = new Certificate();
        c.setId(rs.getInt("id"));
        c.setStudentProfileId(rs.getInt("student_profile_id"));
        int sid = rs.getInt("skill_id");
        if (!rs.wasNull()) c.setSkillId(sid);
        c.setSkillName(rs.getString("skill_name"));
        c.setName(rs.getString("name"));
        c.setIssuingOrganization(rs.getString("issuing_organization"));
        c.setCertificateUrl(rs.getString("certificate_url"));
        c.setIssueDate(rs.getString("issue_date"));
        return c;
    }
}
