-- =============================================================================
-- GovSkill Connect - Government Skill & Employment Portal
-- Relational Database Schema (MySQL Compatible)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS govskill_connect
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE govskill_connect;

-- -----------------------------------------------------------------------------
-- 1. Table: users
-- Core authentication and user account table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(191) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role ENUM('STUDENT', 'EMPLOYER', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    state VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. Table: student_profiles
-- Student detailed onboarding and profile settings
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    date_of_birth DATE NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY') DEFAULT 'PREFER_NOT_TO_SAY',
    category ENUM('GENERAL', 'OBC', 'SC', 'ST', 'EWS') DEFAULT 'GENERAL',
    branch VARCHAR(100) NULL,
    education_level VARCHAR(100) NULL,
    college VARCHAR(200) NULL,
    graduation_year INT NULL,
    bio TEXT NULL,
    profile_visibility BOOLEAN NOT NULL DEFAULT TRUE,
    consent_given BOOLEAN NOT NULL DEFAULT TRUE,
    profile_completion INT NOT NULL DEFAULT 20,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_student_visibility (consent_given, profile_visibility),
    INDEX idx_student_branch (branch),
    INDEX idx_student_education (education_level)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. Table: education
-- Multi-record academic background for students
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS education (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id INT NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    institution VARCHAR(200) NOT NULL,
    branch VARCHAR(100) NULL,
    percentage DECIMAL(5,2) NOT NULL,
    passing_year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    INDEX idx_education_student (student_profile_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. Table: skills
-- Student technical and soft skills with verification state
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') NOT NULL DEFAULT 'INTERMEDIATE',
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    INDEX idx_skills_student (student_profile_id),
    INDEX idx_skills_name (name)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. Table: certificates
-- Proof documents uploaded to verify student skills
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS certificates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id INT NOT NULL,
    skill_id INT NULL,
    name VARCHAR(200) NOT NULL,
    issuing_organization VARCHAR(200) NOT NULL,
    certificate_url VARCHAR(500) NOT NULL,
    issue_date DATE NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE SET NULL,
    INDEX idx_cert_student (student_profile_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. Table: employer_profiles
-- Detailed organization information for employers
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employer_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    organization_name VARCHAR(200) NOT NULL,
    organization_type ENUM('GOVERNMENT', 'PSU', 'PRIVATE', 'NGO', 'ACADEMIC') NOT NULL DEFAULT 'PRIVATE',
    description TEXT NULL,
    website VARCHAR(255) NULL,
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_employer_verified (verification_status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 7. Table: opportunities
-- Jobs, Internships, Apprenticeships, Scholarships, Skill Programs
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS opportunities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employer_id INT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    type ENUM('GOVERNMENT_JOB', 'PRIVATE_JOB', 'INTERNSHIP', 'APPRENTICESHIP', 'SCHOLARSHIP', 'SKILL_DEVELOPMENT') NOT NULL,
    organization VARCHAR(200) NOT NULL,
    location VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    branch VARCHAR(150) NULL,
    education_requirement VARCHAR(150) NOT NULL,
    skills TEXT NOT NULL,
    category VARCHAR(100) NOT NULL DEFAULT 'ALL',
    stipend_or_salary VARCHAR(100) NOT NULL,
    deadline DATE NOT NULL,
    source_url VARCHAR(500) NULL,
    verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES employer_profiles(id) ON DELETE SET NULL,
    INDEX idx_opp_type (type),
    INDEX idx_opp_location (state, city),
    INDEX idx_opp_deadline (deadline)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 8. Table: applications
-- Student job and scheme applications with status tracking
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    opportunity_id INT NOT NULL,
    student_id INT NOT NULL,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'REJECTED', 'SELECTED') NOT NULL DEFAULT 'APPLIED',
    cover_note TEXT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_opportunity (opportunity_id, student_id),
    FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    INDEX idx_app_student (student_id),
    INDEX idx_app_opportunity (opportunity_id),
    INDEX idx_app_status (status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 9. Table: saved_opportunities
-- Bookmarked opportunities by students
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS saved_opportunities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    opportunity_id INT NOT NULL,
    student_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_saved (opportunity_id, student_id),
    FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 10. Table: notifications
-- User notification inbox
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id, is_read)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 11. Table: government_programs
-- Official Central & State Government Welfare, Skill & Scholarship Schemes
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS government_programs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    department VARCHAR(250) NOT NULL,
    type ENUM('SCHOLARSHIP', 'SKILL_DEVELOPMENT', 'EMPLOYMENT_SCHEME', 'APPRENTICESHIP') NOT NULL,
    description TEXT NOT NULL,
    eligibility TEXT NOT NULL,
    benefits TEXT NOT NULL,
    application_url VARCHAR(500) NOT NULL,
    deadline VARCHAR(100) NOT NULL DEFAULT 'Ongoing',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_prog_type (type)
) ENGINE=InnoDB;

-- =============================================================================
-- UPGRADED MODULE SCHEMAS (Advanced Features A through L)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 12. Table: skill_resources (Feature E: Skill Development Hub)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skill_resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    resource_type ENUM('VIDEO', 'ARTICLE', 'NOTES', 'PRACTICE', 'PROJECT') NOT NULL DEFAULT 'VIDEO',
    url VARCHAR(500) NOT NULL,
    duration_minutes INT DEFAULT 0,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
    source VARCHAR(150) NOT NULL,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_resource_skill (skill_name)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 13. Table: student_learning_progress (Feature E & K: Progress Tracking)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_learning_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    resource_id INT NOT NULL,
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
    progress_pct INT DEFAULT 0,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    INDEX idx_learning_student (student_profile_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 14. Table: assessments (Feature F: Online Certification Engine)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS assessments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    total_questions INT NOT NULL DEFAULT 10,
    passing_percentage INT NOT NULL DEFAULT 60,
    duration_minutes INT NOT NULL DEFAULT 20,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'INTERMEDIATE',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_assessment_skill (skill_name)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 15. Table: assessment_questions (Feature F: Proctored Question Bank)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS assessment_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    assessment_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    explanation TEXT NULL,
    marks INT DEFAULT 1,
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 16. Table: student_assessment_attempts (Feature F & G: Anti-Cheat Proctoring)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_assessment_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id INT NOT NULL,
    assessment_id INT NOT NULL,
    score INT NOT NULL,
    total_marks INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    passed BOOLEAN NOT NULL,
    tab_switches INT DEFAULT 0,
    fullscreen_exits INT DEFAULT 0,
    flagged BOOLEAN DEFAULT FALSE,
    flag_reason VARCHAR(255) NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
    INDEX idx_attempt_student (student_profile_id)
) ENGINE=InnoDB;



-- -----------------------------------------------------------------------------
-- 18. Table: interview_questions_bank (Feature J: Voice Interview Engine)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS interview_questions_bank (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category ENUM('HR', 'TECHNICAL', 'BEHAVIORAL', 'GOVERNMENT_VALUES') NOT NULL,
    skill_area VARCHAR(100) NULL,
    question_text TEXT NOT NULL,
    ideal_keywords TEXT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_interview_category (category)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 19. Table: voice_practice_sessions (Feature J: Voice Session History)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS voice_practice_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    questions_attempted INT DEFAULT 0,
    feedback_score INT NULL,
    notes TEXT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB;
