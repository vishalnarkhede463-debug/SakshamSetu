package com.govskill.dao;

import com.govskill.models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public StudentProfile findByUserId(int userId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT sp.*, u.name, u.email, u.phone, u.state, u.city " +
                         "FROM student_profiles sp JOIN users u ON sp.user_id = u.id WHERE sp.user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        StudentProfile sp = mapStudentProfile(rs);
                        populateSubItems(sp);
                        return sp;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (StudentProfile sp : DBConnection.store.studentProfiles.values()) {
            if (sp.getUserId() == userId) {
                populateSubItems(sp);
                return sp;
            }
        }
        return null;
    }

    public StudentProfile findById(int id) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT sp.*, u.name, u.email, u.phone, u.state, u.city " +
                         "FROM student_profiles sp JOIN users u ON sp.user_id = u.id WHERE sp.id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        StudentProfile sp = mapStudentProfile(rs);
                        populateSubItems(sp);
                        return sp;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        StudentProfile sp = DBConnection.store.studentProfiles.get(id);
        if (sp != null) populateSubItems(sp);
        return sp;
    }

    public StudentProfile create(StudentProfile sp) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO student_profiles (user_id, date_of_birth, gender, category, branch, education_level, college, graduation_year, bio, profile_visibility, consent_given, profile_completion) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, sp.getUserId());
                ps.setString(2, sp.getDateOfBirth());
                ps.setString(3, sp.getGender());
                ps.setString(4, sp.getCategory());
                ps.setString(5, sp.getBranch());
                ps.setString(6, sp.getEducationLevel());
                ps.setString(7, sp.getCollege());
                if (sp.getGraduationYear() != null) ps.setInt(8, sp.getGraduationYear()); else ps.setNull(8, Types.INTEGER);
                ps.setString(9, sp.getBio());
                ps.setBoolean(10, sp.isProfileVisibility());
                ps.setBoolean(11, sp.isConsentGiven());
                ps.setInt(12, calculateCompletion(sp));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) sp.setId(rs.getInt(1));
                }
                return sp;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.studentIdSeq.incrementAndGet();
        sp.setId(newId);
        sp.setProfileCompletion(calculateCompletion(sp));
        User u = DBConnection.store.users.get(sp.getUserId());
        if (u != null) {
            sp.setName(u.getName());
            sp.setEmail(u.getEmail());
            sp.setPhone(u.getPhone());
            sp.setState(u.getState());
            sp.setCity(u.getCity());
        }
        DBConnection.store.studentProfiles.put(newId, sp);
        return sp;
    }

    public boolean update(StudentProfile sp) {
        sp.setProfileCompletion(calculateCompletion(sp));
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE student_profiles SET date_of_birth = ?, gender = ?, category = ?, branch = ?, education_level = ?, " +
                         "college = ?, graduation_year = ?, bio = ?, profile_visibility = ?, consent_given = ?, profile_completion = ? " +
                         "WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sp.getDateOfBirth());
                ps.setString(2, sp.getGender());
                ps.setString(3, sp.getCategory());
                ps.setString(4, sp.getBranch());
                ps.setString(5, sp.getEducationLevel());
                ps.setString(6, sp.getCollege());
                if (sp.getGraduationYear() != null) ps.setInt(7, sp.getGraduationYear()); else ps.setNull(7, Types.INTEGER);
                ps.setString(8, sp.getBio());
                ps.setBoolean(9, sp.isProfileVisibility());
                ps.setBoolean(10, sp.isConsentGiven());
                ps.setInt(11, sp.getProfileCompletion());
                ps.setInt(12, sp.getId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        DBConnection.store.studentProfiles.put(sp.getId(), sp);
        return true;
    }

    public List<Education> getEducation(int studentProfileId) {
        List<Education> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM education WHERE student_profile_id = ? ORDER BY passing_year DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentProfileId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Education(
                                rs.getInt("id"),
                                rs.getInt("student_profile_id"),
                                rs.getString("qualification"),
                                rs.getString("institution"),
                                rs.getString("branch"),
                                rs.getDouble("percentage"),
                                rs.getInt("passing_year")
                        ));
                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Education e : DBConnection.store.educations.values()) {
            if (e.getStudentProfileId() == studentProfileId) {
                list.add(e);
            }
        }
        return list;
    }

    public Education addEducation(Education edu) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO education (student_profile_id, qualification, institution, branch, percentage, passing_year) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, edu.getStudentProfileId());
                ps.setString(2, edu.getQualification());
                ps.setString(3, edu.getInstitution());
                ps.setString(4, edu.getBranch());
                ps.setDouble(5, edu.getPercentage());
                ps.setInt(6, edu.getPassingYear());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) edu.setId(rs.getInt(1));
                }
                refreshCompletion(edu.getStudentProfileId());
                return edu;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.eduIdSeq.incrementAndGet();
        edu.setId(newId);
        DBConnection.store.educations.put(newId, edu);
        refreshCompletion(edu.getStudentProfileId());
        return edu;
    }

    public boolean deleteEducation(int id, int studentProfileId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "DELETE FROM education WHERE id = ? AND student_profile_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setInt(2, studentProfileId);
                boolean ok = ps.executeUpdate() > 0;
                refreshCompletion(studentProfileId);
                return ok;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Education e = DBConnection.store.educations.get(id);
        if (e != null && e.getStudentProfileId() == studentProfileId) {
            DBConnection.store.educations.remove(id);
            refreshCompletion(studentProfileId);
            return true;
        }
        return false;
    }

    public List<Skill> getSkills(int studentProfileId) {
        List<Skill> list = new ArrayList<>();
        if (DBConnection.isMySqlAvailable()) {
            String sql = "SELECT * FROM skills WHERE student_profile_id = ? ORDER BY name ASC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentProfileId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Skill(
                                rs.getInt("id"),
                                rs.getInt("student_profile_id"),
                                rs.getString("name"),
                                rs.getString("level"),
                                rs.getBoolean("verified")
                        ));
                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Skill s : DBConnection.store.skills.values()) {
            if (s.getStudentProfileId() == studentProfileId) {
                list.add(s);
            }
        }
        return list;
    }

    public Skill addSkill(Skill skill) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "INSERT INTO skills (student_profile_id, name, level, verified) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, skill.getStudentProfileId());
                ps.setString(2, skill.getName());
                ps.setString(3, skill.getLevel());
                ps.setBoolean(4, skill.isVerified());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) skill.setId(rs.getInt(1));
                }
                refreshCompletion(skill.getStudentProfileId());
                return skill;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int newId = DBConnection.store.skillIdSeq.incrementAndGet();
        skill.setId(newId);
        DBConnection.store.skills.put(newId, skill);
        refreshCompletion(skill.getStudentProfileId());
        return skill;
    }

    public boolean deleteSkill(int id, int studentProfileId) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "DELETE FROM skills WHERE id = ? AND student_profile_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setInt(2, studentProfileId);
                boolean ok = ps.executeUpdate() > 0;
                refreshCompletion(studentProfileId);
                return ok;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Skill s = DBConnection.store.skills.get(id);
        if (s != null && s.getStudentProfileId() == studentProfileId) {
            DBConnection.store.skills.remove(id);
            refreshCompletion(studentProfileId);
            return true;
        }
        return false;
    }

    /**
     * Updates verification status of a skill when certificate is added or removed
     */
    public void updateSkillVerification(int skillId, boolean verified) {
        if (DBConnection.isMySqlAvailable()) {
            String sql = "UPDATE skills SET verified = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBoolean(1, verified);
                ps.setInt(2, skillId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Skill s = DBConnection.store.skills.get(skillId);
        if (s != null) {
            s.setVerified(verified);
        }
    }

    /**
     * Candidate search strictly enforcing consent_given = true AND profile_visibility = true
     */
    public List<StudentProfile> searchCandidates(String skillQuery, String branchQuery, String eduQuery, String locQuery, String catQuery) {
        List<StudentProfile> result = new ArrayList<>();

        if (DBConnection.isMySqlAvailable()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT sp.*, u.name, u.email, u.phone, u.state, u.city " +
                    "FROM student_profiles sp JOIN users u ON sp.user_id = u.id " +
                    "WHERE sp.consent_given = TRUE AND sp.profile_visibility = TRUE "
            );
            List<Object> params = new ArrayList<>();

            if (branchQuery != null && !branchQuery.trim().isEmpty()) {
                sql.append("AND LOWER(sp.branch) LIKE ? ");
                params.add("%" + branchQuery.trim().toLowerCase() + "%");
            }
            if (eduQuery != null && !eduQuery.trim().isEmpty()) {
                sql.append("AND LOWER(sp.education_level) LIKE ? ");
                params.add("%" + eduQuery.trim().toLowerCase() + "%");
            }
            if (locQuery != null && !locQuery.trim().isEmpty()) {
                sql.append("AND (LOWER(u.state) LIKE ? OR LOWER(u.city) LIKE ?) ");
                params.add("%" + locQuery.trim().toLowerCase() + "%");
                params.add("%" + locQuery.trim().toLowerCase() + "%");
            }
            if (catQuery != null && !catQuery.trim().isEmpty() && !"ALL".equalsIgnoreCase(catQuery)) {
                sql.append("AND sp.category = ? ");
                params.add(catQuery.trim().toUpperCase());
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StudentProfile sp = mapStudentProfile(rs);
                        populateSubItems(sp);
                        // Filter skill if queried
                        if (skillQuery != null && !skillQuery.trim().isEmpty()) {
                            boolean hasSkill = false;
                            for (Skill s : sp.getSkillsList()) {
                                if (s.getName().toLowerCase().contains(skillQuery.trim().toLowerCase())) {
                                    hasSkill = true;
                                    break;
                                }
                            }
                            if (!hasSkill) continue;
                        }
                        result.add(sp);
                    }
                    return result;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Fallback candidate search
        for (StudentProfile sp : DBConnection.store.studentProfiles.values()) {
            // STRICT PRIVACY CHECK: Only show students who gave consent and have visibility ON
            if (!sp.isConsentGiven() || !sp.isProfileVisibility()) {
                continue;
            }

            if (branchQuery != null && !branchQuery.trim().isEmpty()) {
                if (sp.getBranch() == null || !sp.getBranch().toLowerCase().contains(branchQuery.trim().toLowerCase())) continue;
            }
            if (eduQuery != null && !eduQuery.trim().isEmpty()) {
                if (sp.getEducationLevel() == null || !sp.getEducationLevel().toLowerCase().contains(eduQuery.trim().toLowerCase())) continue;
            }
            if (locQuery != null && !locQuery.trim().isEmpty()) {
                String loc = (sp.getState() + " " + sp.getCity()).toLowerCase();
                if (!loc.contains(locQuery.trim().toLowerCase())) continue;
            }
            if (catQuery != null && !catQuery.trim().isEmpty() && !"ALL".equalsIgnoreCase(catQuery)) {
                if (!catQuery.equalsIgnoreCase(sp.getCategory())) continue;
            }

            populateSubItems(sp);

            if (skillQuery != null && !skillQuery.trim().isEmpty()) {
                boolean hasSkill = false;
                for (Skill s : sp.getSkillsList()) {
                    if (s.getName().toLowerCase().contains(skillQuery.trim().toLowerCase())) {
                        hasSkill = true;
                        break;
                    }
                }
                if (!hasSkill) continue;
            }

            result.add(sp);
        }

        return result;
    }

    private void populateSubItems(StudentProfile sp) {
        sp.setEducationList(getEducation(sp.getId()));
        sp.setSkillsList(getSkills(sp.getId()));

        CertificateDAO certDao = new CertificateDAO();
        sp.setCertificatesList(certDao.findByStudentProfileId(sp.getId()));
    }

    private void refreshCompletion(int studentProfileId) {
        StudentProfile sp = findById(studentProfileId);
        if (sp != null) {
            update(sp);
        }
    }

    public int calculateCompletion(StudentProfile sp) {
        int score = 20; // Baseline registration
        if (sp.getDateOfBirth() != null && !sp.getDateOfBirth().isEmpty()) score += 10;
        if (sp.getBranch() != null && !sp.getBranch().isEmpty()) score += 15;
        if (sp.getEducationLevel() != null && !sp.getEducationLevel().isEmpty()) score += 15;
        if (sp.getCollege() != null && !sp.getCollege().isEmpty()) score += 10;
        if (sp.getBio() != null && sp.getBio().length() > 10) score += 10;
        if (sp.isConsentGiven()) score += 10;
        if (sp.getSkillsList() != null && !sp.getSkillsList().isEmpty()) score += 10;
        return Math.min(100, score);
    }

    private StudentProfile mapStudentProfile(ResultSet rs) throws SQLException {
        StudentProfile sp = new StudentProfile();
        sp.setId(rs.getInt("id"));
        sp.setUserId(rs.getInt("user_id"));
        sp.setDateOfBirth(rs.getString("date_of_birth"));
        sp.setGender(rs.getString("gender"));
        sp.setCategory(rs.getString("category"));
        sp.setBranch(rs.getString("branch"));
        sp.setEducationLevel(rs.getString("education_level"));
        sp.setCollege(rs.getString("college"));
        int gradYear = rs.getInt("graduation_year");
        if (!rs.wasNull()) sp.setGraduationYear(gradYear);
        sp.setBio(rs.getString("bio"));
        sp.setProfileVisibility(rs.getBoolean("profile_visibility"));
        sp.setConsentGiven(rs.getBoolean("consent_given"));
        sp.setProfileCompletion(rs.getInt("profile_completion"));
        sp.setName(rs.getString("name"));
        sp.setEmail(rs.getString("email"));
        sp.setPhone(rs.getString("phone"));
        sp.setState(rs.getString("state"));
        sp.setCity(rs.getString("city"));
        return sp;
    }
}
