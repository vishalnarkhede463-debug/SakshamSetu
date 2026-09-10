-- =============================================================================
-- GovSkill Connect - Government Skill & Employment Portal
-- Seed Data: 25+ Opportunities, 10 Students, 5 Employers, 12 Govt Programs, 20+ Applications
-- =============================================================================

USE govskill_connect;

-- -----------------------------------------------------------------------------
-- 1. Seed Users (Pre-hashed SHA-256 password: password + salt1234)
-- Passwords:
-- admin@govskill.gov.in -> admin123
-- hr@isro.gov.in -> employer123
-- hr@bhel.in -> employer123
-- careers@tcs.com -> employer123
-- talent@infosys.com -> employer123
-- rahul.sharma@gmail.com -> student123
-- priya.patil@gmail.com -> student123
-- aniket.deshmukh@gmail.com -> student123
-- sneha.kulkarni@gmail.com -> student123
-- amit.verma@gmail.com -> student123
-- neha.singh@gmail.com -> student123
-- rohit.jadhav@gmail.com -> student123
-- pooja.shinde@gmail.com -> student123
-- vikram.nair@gmail.com -> student123
-- kavita.joshi@gmail.com -> student123
-- -----------------------------------------------------------------------------

INSERT INTO users (id, name, email, phone, password_hash, salt, role, state, city) VALUES
(1, 'National Portal Administrator', 'admin@govskill.gov.in', '9811001122', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'ADMIN', 'Delhi', 'New Delhi'),
(2, 'ISRO Recruitment Cell', 'hr@isro.gov.in', '9822001122', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'EMPLOYER', 'Karnataka', 'Bengaluru'),
(3, 'BHEL Apprenticeship Division', 'hr@bhel.in', '9833001122', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'EMPLOYER', 'Delhi', 'New Delhi'),
(4, 'Tata Consultancy Services', 'careers@tcs.com', '9844001122', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'EMPLOYER', 'Maharashtra', 'Mumbai'),
(5, 'Infosys Springboard Talent', 'talent@infosys.com', '9855001122', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'EMPLOYER', 'Karnataka', 'Bengaluru'),
(6, 'Maharashtra State Innovation Society', 'info@msins.gov.in', '9866001122', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'EMPLOYER', 'Maharashtra', 'Pune'),
-- Students
(7, 'Rahul Sharma', 'rahul.sharma@gmail.com', '9911223344', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Pune'),
(8, 'Priya Patil', 'priya.patil@gmail.com', '9922334455', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Mumbai'),
(9, 'Aniket Deshmukh', 'aniket.deshmukh@gmail.com', '9933445566', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Nagpur'),
(10, 'Sneha Kulkarni', 'sneha.kulkarni@gmail.com', '9944556677', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Nashik'),
(11, 'Amit Verma', 'amit.verma@gmail.com', '9955667788', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Delhi', 'Delhi'),
(12, 'Neha Singh', 'neha.singh@gmail.com', '9966778899', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Karnataka', 'Bengaluru'),
(13, 'Rohit Jadhav', 'rohit.jadhav@gmail.com', '9977889900', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Thane'),
(14, 'Pooja Shinde', 'pooja.shinde@gmail.com', '9988990011', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Navi Mumbai'),
(15, 'Vikram Nair', 'vikram.nair@gmail.com', '9900112233', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Telangana', 'Hyderabad'),
(16, 'Kavita Joshi', 'kavita.joshi@gmail.com', '9912345678', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'salt1234', 'STUDENT', 'Maharashtra', 'Pune');

-- -----------------------------------------------------------------------------
-- 2. Seed Employer Profiles
-- -----------------------------------------------------------------------------
INSERT INTO employer_profiles (id, user_id, organization_name, organization_type, description, website, verification_status) VALUES
(1, 2, 'Indian Space Research Organisation (ISRO)', 'GOVERNMENT', 'Primary space exploration agency of the Government of India.', 'https://www.isro.gov.in', 'VERIFIED'),
(2, 3, 'Bharat Heavy Electricals Limited (BHEL)', 'PSU', 'India largest engineering and manufacturing company of its kind.', 'https://www.bhel.in', 'VERIFIED'),
(3, 4, 'Tata Consultancy Services Ltd', 'PRIVATE', 'Leading global IT services, consulting and business solutions provider.', 'https://www.tcs.com', 'VERIFIED'),
(4, 5, 'Infosys Springboard Division', 'PRIVATE', 'Pioneering global digital transformation and technology partner.', 'https://www.infosys.com', 'VERIFIED'),
(5, 6, 'Maharashtra State Innovation Society', 'GOVERNMENT', 'Nodal agency for promoting startup and innovation ecosystem in Maharashtra.', 'https://msins.in', 'VERIFIED');

-- -----------------------------------------------------------------------------
-- 3. Seed Student Profiles
-- -----------------------------------------------------------------------------
INSERT INTO student_profiles (id, user_id, date_of_birth, gender, category, branch, education_level, college, graduation_year, bio, profile_visibility, consent_given, profile_completion) VALUES
(1, 7, '2002-05-14', 'MALE', 'OBC', 'Computer Science & Engineering', 'Undergraduate', 'College of Engineering Pune (COEP)', 2024, 'Passionate software developer interested in Java, C++, and Distributed Systems.', TRUE, TRUE, 90),
(2, 8, '2003-08-22', 'FEMALE', 'GENERAL', 'Information Technology', 'Undergraduate', 'Veermata Jijabai Technological Institute (VJTI)', 2025, 'Full stack web developer and cloud enthusiast aiming for research opportunities.', TRUE, TRUE, 85),
(3, 9, '2001-11-10', 'MALE', 'SC', 'Mechanical Engineering', 'Undergraduate', 'VNIT Nagpur', 2023, 'CAD modeling, thermal analysis and robotics automation specialist.', TRUE, TRUE, 80),
(4, 10, '2002-03-18', 'FEMALE', 'OBC', 'Electronics & Telecommunication', 'Undergraduate', 'K.K. Wagh Institute of Engineering', 2024, 'IoT, Embedded Systems, and Signal Processing enthusiast.', TRUE, TRUE, 75),
(5, 11, '2000-09-05', 'MALE', 'GENERAL', 'Computer Science', 'Postgraduate', 'Delhi Technological University (DTU)', 2023, 'Machine learning researcher and data science practitioner.', TRUE, TRUE, 95),
(6, 12, '2003-01-25', 'FEMALE', 'EWS', 'Data Science & AI', 'Undergraduate', 'BMS College of Engineering', 2025, 'Python and Data Analytics learner looking for internships.', TRUE, TRUE, 70),
(7, 13, '2002-07-12', 'MALE', 'GENERAL', 'Electrical Engineering', 'Diploma', 'Government Polytechnic Thane', 2023, 'Power systems, industrial wiring, and PLC automation.', FALSE, FALSE, 65), -- Consent OFF (Private profile test)
(8, 14, '2001-12-30', 'FEMALE', 'ST', 'Civil Engineering', 'Undergraduate', 'DY Patil College of Engineering', 2024, 'Structural planning, AutoCAD, and green building projects.', TRUE, TRUE, 85),
(9, 15, '2002-04-16', 'MALE', 'GENERAL', 'Computer Science & Engineering', 'Undergraduate', 'JNTU Hyderabad', 2024, 'Cyber security, network defense, and ethical hacking.', TRUE, TRUE, 90),
(10, 16, '2003-10-08', 'FEMALE', 'OBC', 'Information Technology', 'Undergraduate', 'MIT World Peace University', 2025, 'Web development and digital marketing enthusiast.', TRUE, TRUE, 80);

-- -----------------------------------------------------------------------------
-- 4. Seed Education
-- -----------------------------------------------------------------------------
INSERT INTO education (id, student_profile_id, qualification, institution, branch, percentage, passing_year) VALUES
(1, 1, 'Undergraduate', 'College of Engineering Pune (COEP)', 'Computer Science & Engineering', 88.50, 2024),
(2, 1, '12th', 'Modern Junior College Pune', 'Science', 91.20, 2020),
(3, 2, 'Undergraduate', 'Veermata Jijabai Technological Institute (VJTI)', 'Information Technology', 86.40, 2025),
(4, 3, 'Undergraduate', 'VNIT Nagpur', 'Mechanical Engineering', 82.10, 2023),
(5, 4, 'Undergraduate', 'K.K. Wagh Institute of Engineering', 'Electronics & Telecommunication', 84.70, 2024),
(6, 5, 'Postgraduate', 'Delhi Technological University (DTU)', 'Computer Science', 89.00, 2023),
(7, 6, 'Undergraduate', 'BMS College of Engineering', 'Data Science & AI', 81.50, 2025),
(8, 7, 'Diploma', 'Government Polytechnic Thane', 'Electrical Engineering', 79.30, 2023),
(9, 8, 'Undergraduate', 'DY Patil College of Engineering', 'Civil Engineering', 83.20, 2024),
(10, 9, 'Undergraduate', 'JNTU Hyderabad', 'Computer Science & Engineering', 87.60, 2024),
(11, 10, 'Undergraduate', 'MIT World Peace University', 'Information Technology', 85.00, 2025);

-- -----------------------------------------------------------------------------
-- 5. Seed Skills
-- -----------------------------------------------------------------------------
INSERT INTO skills (id, student_profile_id, name, level, verified) VALUES
(1, 1, 'Java', 'ADVANCED', TRUE),
(2, 1, 'C++', 'ADVANCED', TRUE),
(3, 1, 'SQL', 'ADVANCED', TRUE),
(4, 1, 'Web Development', 'INTERMEDIATE', FALSE),
(5, 2, 'JavaScript', 'ADVANCED', TRUE),
(6, 2, 'HTML', 'EXPERT', TRUE),
(7, 2, 'CSS', 'EXPERT', TRUE),
(8, 2, 'Python', 'INTERMEDIATE', FALSE),
(9, 3, 'AutoCAD', 'ADVANCED', TRUE),
(10, 3, 'Communication', 'ADVANCED', TRUE),
(11, 4, 'C', 'ADVANCED', TRUE),
(12, 4, 'Cyber Security', 'BEGINNER', FALSE),
(13, 5, 'Python', 'EXPERT', TRUE),
(14, 5, 'AI/ML', 'EXPERT', TRUE),
(15, 5, 'Data Analytics', 'ADVANCED', TRUE),
(16, 6, 'Data Analytics', 'INTERMEDIATE', FALSE),
(17, 7, 'PLC Automation', 'INTERMEDIATE', FALSE),
(18, 8, 'Civil Engineering', 'ADVANCED', TRUE),
(19, 9, 'Cyber Security', 'ADVANCED', TRUE),
(20, 9, 'Java', 'INTERMEDIATE', TRUE),
(21, 10, 'Digital Marketing', 'INTERMEDIATE', TRUE),
(22, 10, 'Web Development', 'INTERMEDIATE', FALSE);

-- -----------------------------------------------------------------------------
-- 6. Seed Certificates
-- -----------------------------------------------------------------------------
INSERT INTO certificates (id, student_profile_id, skill_id, name, issuing_organization, certificate_url, issue_date) VALUES
(1, 1, 1, 'Oracle Certified Professional: Java SE 11', 'Oracle University', '/uploads/cert_oracle_java.pdf', '2023-06-15'),
(2, 1, 2, 'C++ High Performance Certificate', 'NPTEL / IIT Bombay', '/uploads/cert_nptel_cpp.pdf', '2023-09-20'),
(3, 1, 3, 'Relational Database Management Systems with SQL', 'IIT Kharagpur', '/uploads/cert_dbms_sql.pdf', '2023-11-10'),
(4, 2, 5, 'Modern JavaScript Fundamentals', 'FreeCodeCamp', '/uploads/cert_fcc_js.pdf', '2024-02-14'),
(5, 2, 6, 'Responsive Web Design Certification', 'W3C / edX', '/uploads/cert_w3c_html.pdf', '2024-01-10'),
(6, 3, 9, 'Certified SolidWorks Associate', 'Dassault Systèmes', '/uploads/cert_solidworks.pdf', '2022-12-05'),
(7, 4, 11, 'Embedded C and Microcontroller Programming', 'CDAC Pune', '/uploads/cert_cdac_c.pdf', '2023-08-18'),
(8, 5, 13, 'Python for Data Science and Machine Learning', 'IIT Madras', '/uploads/cert_nptel_python.pdf', '2023-05-12'),
(9, 5, 14, 'Deep Learning Specialization Certificate', 'DeepLearning.AI', '/uploads/cert_dl_specialization.pdf', '2023-07-28'),
(10, 9, 19, 'Certified Ethical Hacker (CEH v12)', 'EC-Council', '/uploads/cert_ceh.pdf', '2024-03-01');

-- -----------------------------------------------------------------------------
-- 7. Seed Opportunities (25 records across Indian locations and sectors)
-- -----------------------------------------------------------------------------
INSERT INTO opportunities (id, employer_id, title, description, type, organization, location, state, city, branch, education_requirement, skills, category, stipend_or_salary, deadline, source_url, verified) VALUES
(1, 1, 'Junior Scientist / Engineer (Computer Science)', 'Recruitment for ICRB Engineer positions to support satellite data ground stations and telemetry networks.', 'GOVERNMENT_JOB', 'ISRO - Satellite Centre', 'Bengaluru', 'Karnataka', 'Bengaluru', 'Computer Science & Engineering', 'B.Tech / B.E.', 'Java, C++, Data Structures, Algorithms, Linux', 'GENERAL', 'INR 56,100 - 1,77,500 / month', '2026-11-30', 'https://www.isro.gov.in/careers', TRUE),
(2, 2, 'Graduate Apprenticeship Trainee (Mechanical)', 'One year National Apprenticeship Training (NATS) for fresh engineering graduates in turbine manufacturing.', 'APPRENTICESHIP', 'Bharat Heavy Electricals Limited (BHEL)', 'New Delhi', 'Delhi', 'New Delhi', 'Mechanical Engineering', 'B.Tech / B.E. / Diploma', 'AutoCAD, Thermal Engineering, Manufacturing Processes', 'ALL', 'INR 15,000 / month stipend', '2026-10-15', 'https://www.bhel.in/apprenticeship', TRUE),
(3, 3, 'Systems Engineer Trainee', 'Software development, testing, and deployment for enterprise banking and government digital applications.', 'PRIVATE_JOB', 'Tata Consultancy Services', 'Pune', 'Maharashtra', 'Pune', 'Computer Science, IT, Electronics', 'B.Tech / B.E. / MCA', 'Java, SQL, JavaScript, HTML, CSS', 'ALL', 'INR 4.5 LPA - 7.0 LPA', '2026-12-15', 'https://www.tcs.com/careers', TRUE),
(4, 5, 'Prime Minister Internship Scheme 2026', 'Government-backed 12-month internship in leading enterprise setups offering industrial exposure and monthly financial assistance.', 'INTERNSHIP', 'Ministry of Corporate Affairs / MSInS', 'Mumbai', 'Maharashtra', 'Mumbai', 'All Engineering & Technology Branches', 'Diploma / Undergraduate', 'Communication, Web Development, Digital Marketing, MS Office', 'ALL', 'INR 5,000 / month stipend + INR 6,000 grant', '2026-10-31', 'https://pminternship.mca.gov.in', TRUE),
(5, 1, 'Technical Assistant (Electronics & Communication)', 'Operation, maintenance, and testing of high-frequency telemetry receivers and space payload testing rigs.', 'GOVERNMENT_JOB', 'ISRO - Space Applications Centre', 'Bengaluru', 'Karnataka', 'Bengaluru', 'Electronics & Telecommunication', 'Diploma in Engineering', 'C, Embedded Systems, Microcontrollers, Circuit Design', 'OBC', 'INR 44,900 - 1,42,400 / month', '2026-11-20', 'https://www.isro.gov.in/sac', TRUE),
(6, 4, 'Data Analytics Intern', 'Work closely with data engineers to build automated pipelines, visualize telemetry data, and clean datasets.', 'INTERNSHIP', 'Infosys Springboard', 'Bengaluru', 'Karnataka', 'Bengaluru', 'Computer Science, Data Science, IT', 'Undergraduate', 'Python, Data Analytics, SQL, Statistics', 'ALL', 'INR 25,000 / month stipend', '2026-10-28', 'https://springboard.infosys.com', TRUE),
(7, 2, 'Technician Apprentice (Electrical)', 'Shop floor maintenance of high voltage transformers, switchgear, and auxiliary electrical systems under NAPS.', 'APPRENTICESHIP', 'BHEL Electrical Plant', 'Nagpur', 'Maharashtra', 'Nagpur', 'Electrical Engineering', 'Diploma / ITI', 'PLC Automation, Electrical Wiring, Safety Compliance', 'SC', 'INR 12,500 / month stipend', '2026-10-25', 'https://apprenticeshipindia.gov.in', TRUE),
(8, NULL, 'National Post-Matric Scholarship for Higher Education', 'Financial aid for meritorious students from socio-economically weaker backgrounds pursuing degree programs.', 'SCHOLARSHIP', 'Ministry of Social Justice & Empowerment', 'New Delhi', 'Delhi', 'New Delhi', 'Any Branch', '12th / Diploma / Undergraduate', 'Academic Merit, Minimum 60% in previous exam', 'SC', 'INR 1,20,000 / year + tuition waiver', '2026-11-15', 'https://scholarships.gov.in', TRUE),
(9, NULL, 'PMKVY 4.0: Certified Cyber Security Associate', 'Pradhan Mantri Kaushal Vikas Yojana funded 400-hour skill development course with 100% placement support.', 'SKILL_DEVELOPMENT', 'National Skill Development Corporation (NSDC)', 'Pune', 'Maharashtra', 'Pune', 'Any Technical Branch', '12th / Diploma / Degree', 'Cyber Security, Networking, Linux, Firewalls', 'ALL', 'Free Course + Government Certificate + INR 8,000 allowance', '2026-11-10', 'https://www.skillindiadigital.gov.in', TRUE),
(10, 5, 'AI & Machine Learning Research Intern', 'Hands-on exposure to NLP, LLM fine-tuning, and computer vision models for government civic chatbots.', 'INTERNSHIP', 'Maharashtra State Innovation Society', 'Pune', 'Maharashtra', 'Pune', 'Computer Science, AI, Mathematics', 'B.Tech / M.Tech / MCA', 'Python, AI/ML, Data Analytics, Deep Learning', 'ALL', 'INR 30,000 / month stipend', '2026-10-30', 'https://msins.in/fellowship', TRUE),
(11, 3, 'Junior Web Developer (Frontend)', 'Develop accessible, multilingual citizen service portal interfaces adhering to GIGW standards.', 'PRIVATE_JOB', 'Tata Consultancy Services', 'Thane', 'Maharashtra', 'Thane', 'Computer Science, IT', 'BCA / B.Sc / B.Tech', 'HTML, CSS, JavaScript, Web Development', 'ALL', 'INR 3.8 LPA - 5.5 LPA', '2026-12-05', 'https://www.tcs.com/careers', TRUE),
(12, NULL, 'Pragati Scholarship Scheme for Girl Students', 'AICTE financial scholarship to empower girl students admitted into technical degree and diploma courses.', 'SCHOLARSHIP', 'AICTE / Ministry of Education', 'New Delhi', 'Delhi', 'New Delhi', 'Engineering / Technology', 'First Year Degree / Diploma', 'Minimum 60% in 12th / 10th', 'FEMALE', 'INR 50,000 / annum', '2026-11-30', 'https://facilities.aicte-india.org/pragati', TRUE),
(13, NULL, 'Apprentice Engineer (Civil & Urban Infrastructure)', 'Site supervision, quality verification, and structural inspection for smart city transportation corridors.', 'APPRENTICESHIP', 'National Highways Authority of India (NHAI)', 'Nashik', 'Maharashtra', 'Nashik', 'Civil Engineering', 'B.Tech / Diploma in Civil', 'AutoCAD, Structural Design, Surveying', 'ALL', 'INR 16,000 / month stipend', '2026-10-20', 'https://nhai.gov.in', TRUE),
(14, 4, 'Cyber Security Analyst Trainee', 'Threat monitoring, vulnerability assessment, and log auditing in Security Operations Center (SOC).', 'PRIVATE_JOB', 'Infosys Cyber Defense Center', 'Hyderabad', 'Telangana', 'Hyderabad', 'Computer Science, IT, Cyber Security', 'B.Tech / B.E.', 'Cyber Security, Python, Networking, Linux', 'ALL', 'INR 5.0 LPA - 8.0 LPA', '2026-12-20', 'https://www.infosys.com/security', TRUE),
(15, NULL, 'PMKVY 4.0: Full Stack Web Developer Certification', 'Government sponsored vocational training curriculum covering HTML, CSS, JavaScript, Java, and Database systems.', 'SKILL_DEVELOPMENT', 'Skill India Digital Hub', 'Navi Mumbai', 'Maharashtra', 'Navi Mumbai', 'Any Stream', '10th / 12th Pass', 'HTML, CSS, JavaScript, Web Development', 'ALL', 'Free Training + Govt NSQF Level 5 Certification', '2026-11-05', 'https://www.skillindiadigital.gov.in', TRUE),
(16, 1, 'Junior Research Fellow (Propulsion Systems)', 'Computational fluid dynamics and propulsion simulation for next generation satellite launch vehicles.', 'GOVERNMENT_JOB', 'DRDO - Defence Research Laboratory', 'Pune', 'Maharashtra', 'Pune', 'Mechanical Engineering, Aerospace', 'B.E. / B.Tech (First Class)', 'C++, MATLAB, CFD, Thermal Engineering', 'GENERAL', 'INR 37,000 / month + HRA', '2026-11-15', 'https://www.drdo.gov.in/careers', TRUE),
(17, 3, 'Java Enterprise Software Engineer', 'Backend development for mission critical railway freight and passenger reservation modules.', 'PRIVATE_JOB', 'TCS Government Projects Business Unit', 'Navi Mumbai', 'Maharashtra', 'Navi Mumbai', 'Computer Science, IT', 'B.Tech / MCA', 'Java, SQL, C++, Spring Boot, Linux', 'ALL', 'INR 6.5 LPA - 9.5 LPA', '2026-12-22', 'https://www.tcs.com/careers', TRUE),
(18, NULL, 'National Overseas Scholarship for SC / ST Candidates', 'Financial assistance to students selected for Masters and Ph.D. programs in top-ranked overseas universities.', 'SCHOLARSHIP', 'Ministry of Tribal Affairs & Social Justice', 'New Delhi', 'Delhi', 'New Delhi', 'Engineering, Pure Sciences, Medicine', 'Undergraduate / Postgraduate', 'Academic Excellence (>60%), University Admit Letter', 'SC', 'Full Tuition + Living Allowance (USD 15,400/yr)', '2026-12-31', 'https://nosmsje.gov.in', TRUE),
(19, 2, 'ITI Trade Apprentice (Fitter / Electrician)', 'Practical hands-on training under the Apprentices Act 1961 inside precision manufacturing bays.', 'APPRENTICESHIP', 'BHEL Manufacturing Complex', 'Hyderabad', 'Telangana', 'Hyderabad', 'Mechanical / Electrical', 'ITI Passed in Relevant Trade', 'Trade Theory, Machine Shop Operations, Electrical Wiring', 'OBC', 'INR 10,500 / month stipend', '2026-10-18', 'https://www.bhel.in/careers', TRUE),
(20, NULL, 'Digital India FutureSkills Prime: AI Data Annotation', 'Certification program for young graduates to master generative AI data labelling, annotation, and prompt testing.', 'SKILL_DEVELOPMENT', 'Ministry of Electronics and IT (MeitY) / NASSCOM', 'Delhi', 'Delhi', 'New Delhi', 'Any Graduate', 'Degree / Diploma Completed', 'Data Analytics, Communication, Basic Computer Literacy', 'ALL', 'Subsidized Fee + NSDC Certification + Job Fair Access', '2026-11-25', 'https://futureskillsprime.in', TRUE),
(21, 5, 'Junior Software Associate', 'Entry level software engineering position focusing on cloud migration and database optimization.', 'PRIVATE_JOB', 'Infosys Technology Labs', 'Pune', 'Maharashtra', 'Pune', 'Computer Science, IT', 'B.Sc (IT) / BCA / B.Tech', 'Java, Python, SQL, Git', 'ALL', 'INR 4.0 LPA - 6.0 LPA', '2026-11-28', 'https://www.infosys.com/careers', TRUE),
(22, 1, 'Scientific Officer / Technical Lead', 'Satellite navigation and atomic clock frequency standard calibration.', 'GOVERNMENT_JOB', 'National Physical Laboratory (CSIR-NPL)', 'Delhi', 'Delhi', 'New Delhi', 'Electronics, Applied Physics', 'M.Sc / M.Tech', 'C, C++, Signal Processing, Instrumentation', 'ALL', 'INR 67,700 - 2,08,700 / month', '2026-12-10', 'https://www.nplindia.in', TRUE),
(23, NULL, 'Post-Matric Scholarship for OBC Students', 'Centrally sponsored scheme providing financial stipend to OBC students pursuing post-matriculation courses.', 'SCHOLARSHIP', 'Department of Social Justice', 'Mumbai', 'Maharashtra', 'Mumbai', 'Any Stream', '10th / 12th / Diploma / Degree', 'OBC Certificate, Annual Family Income < INR 2.5 Lakhs', 'OBC', 'INR 25,000 to 50,000 / year', '2026-11-30', 'https://mahadbt.maharashtra.gov.in', TRUE),
(24, NULL, 'DDU-GKY Rural Youth Skill Training (Solar PV Technician)', 'Deen Dayal Upadhyaya Grameen Kaushalya Yojana residential skilling program for rural youth with placement guarantee.', 'SKILL_DEVELOPMENT', 'Ministry of Rural Development', 'Nagpur', 'Maharashtra', 'Nagpur', 'Electrical / Renewable Energy', '10th / 12th Pass', 'Solar Installation, Electrical Wiring, Maintenance', 'ALL', 'Free Residential Training + Uniform + Placed Employment', '2026-11-12', 'https://ddugky.gov.in', TRUE),
(25, 4, 'Graduate Trainee - Business Systems', 'Requirements gathering, client coordination, and digital reporting for state e-governance solutions.', 'PRIVATE_JOB', 'Tata Consultancy Services', 'Hyderabad', 'Telangana', 'Hyderabad', 'All Engineering Disciplines', 'B.Tech / B.E.', 'Communication, Digital Marketing, MS Excel, SQL', 'ALL', 'INR 4.2 LPA - 5.8 LPA', '2026-12-18', 'https://www.tcs.com/careers', TRUE);

-- -----------------------------------------------------------------------------
-- 8. Seed Applications (20+ records connecting students with opportunities)
-- -----------------------------------------------------------------------------
INSERT INTO applications (id, opportunity_id, student_id, status, cover_note) VALUES
(1, 1, 1, 'SHORTLISTED', 'B.Tech CSE from COEP with strong background in Java and C++. Oracle certified.'),
(2, 3, 1, 'SELECTED', 'Applying for Systems Engineer Trainee role. Strong coding skills in Java and SQL.'),
(3, 4, 1, 'APPLIED', 'Interested in the PM Internship Scheme to gain real-world industrial exposure.'),
(4, 17, 1, 'UNDER_REVIEW', 'Experienced in backend Java development and database systems.'),
(5, 3, 2, 'SHORTLISTED', 'VJTI IT undergraduate skilled in JavaScript, HTML, and modern web frameworks.'),
(6, 4, 2, 'SELECTED', 'Interested in state-level digital e-governance and innovation initiatives.'),
(7, 11, 2, 'UNDER_REVIEW', 'Applying for Junior Web Developer position. Proficient in HTML/CSS/JS.'),
(8, 2, 3, 'SHORTLISTED', 'B.Tech Mechanical from VNIT Nagpur. Certified in SolidWorks and CAD.'),
(9, 7, 3, 'APPLIED', 'Application for Apprenticeship in electrical and manufacturing maintenance.'),
(10, 5, 4, 'UNDER_REVIEW', 'E&TC undergraduate with practical embedded C and microcontroller skills.'),
(11, 16, 4, 'APPLIED', 'Interested in research and defense communication engineering.'),
(12, 6, 5, 'SELECTED', 'M.Tech CSE with publications in machine learning and data pipelines.'),
(13, 10, 5, 'SHORTLISTED', 'Applying for AI & ML Research Intern. Hands-on experience in PyTorch and NLP.'),
(14, 14, 5, 'UNDER_REVIEW', 'Strong background in algorithms, python, and computational analytics.'),
(15, 6, 6, 'APPLIED', 'Data science undergraduate keen on learning enterprise data analytics.'),
(16, 15, 6, 'APPLIED', 'Enrolling in skill development for full stack web development.'),
(17, 7, 7, 'REJECTED', 'Diploma electrical passed. Applied for BHEL plant apprentice.'),
(18, 13, 8, 'SHORTLISTED', 'Civil engineering graduate with strong structural planning skills.'),
(19, 14, 9, 'SHORTLISTED', 'Certified Ethical Hacker with hands-on lab experience in network auditing.'),
(20, 1, 9, 'UNDER_REVIEW', 'Applying for ISRO Engineer position in computer security and networks.'),
(21, 11, 10, 'APPLIED', 'IT undergraduate interested in digital communication and frontend systems.'),
(22, 25, 10, 'SHORTLISTED', 'Skilled in digital marketing, presentation, and data management.');

-- -----------------------------------------------------------------------------
-- 9. Seed Saved Opportunities
-- -----------------------------------------------------------------------------
INSERT INTO saved_opportunities (id, opportunity_id, student_id) VALUES
(1, 1, 1),
(2, 6, 1),
(3, 10, 1),
(4, 11, 2),
(5, 4, 2),
(6, 2, 3),
(7, 1, 5),
(8, 14, 9);

-- -----------------------------------------------------------------------------
-- 10. Seed Notifications
-- -----------------------------------------------------------------------------
INSERT INTO notifications (id, user_id, title, message, is_read) VALUES
(1, 7, 'Application Shortlisted', 'Congratulations! Your application for Junior Scientist / Engineer at ISRO has been shortlisted for technical interview.', FALSE),
(2, 7, 'Application Selected', 'Great news! You have been selected for Systems Engineer Trainee at Tata Consultancy Services.', TRUE),
(3, 7, 'New Matching Opportunity', 'A new Government Job matching your skills (Java, C++) was posted: Junior Research Fellow at DRDO.', TRUE),
(4, 7, 'Profile Completion Reminder', 'Your profile is 90% complete. Add your latest project or publication to reach 100%.', TRUE),
(5, 8, 'Application Selected', 'You have been selected for the Prime Minister Internship Scheme 2026. Please review joining instructions.', FALSE),
(6, 8, 'Skill Verified', 'Your skill "JavaScript" has been verified following document review.', TRUE),
(7, 9, 'Application Shortlisted', 'Your application for Graduate Apprenticeship Trainee at BHEL has been shortlisted.', FALSE),
(8, 11, 'Application Selected', 'You have been selected for the AI & Machine Learning Research Fellowship at MSInS.', FALSE),
(9, 15, 'Application Shortlisted', 'Your application for Cyber Security Analyst Trainee at Infosys has progressed to technical review.', FALSE);

-- -----------------------------------------------------------------------------
-- 11. Seed Government Programs (Central & State Schemes)
-- -----------------------------------------------------------------------------
INSERT INTO government_programs (id, name, department, type, description, eligibility, benefits, application_url, deadline) VALUES
(1, 'Pradhan Mantri Kaushal Vikas Yojana (PMKVY 4.0)', 'Ministry of Skill Development and Entrepreneurship (MSDE)', 'SKILL_DEVELOPMENT', 'Flagship scheme for imparting industry-relevant skill training to Indian youth to enable them to secure better livelihoods.', 'Any Indian citizen aged 15-45 years with basic education (10th/12th/ITI).', '100% Free Training, Government NSQF certification, accident insurance, and direct placement support.', 'https://www.skillindiadigital.gov.in', 'Ongoing - Cycle 2026'),
(2, 'Prime Minister Internship Scheme (PMIS)', 'Ministry of Corporate Affairs', 'EMPLOYMENT_SCHEME', 'Provides 12 months of real-world internship opportunity in top 500 companies across India.', 'Candidates aged 21-24 years not engaged in full-time employment, possessing ITI/Diploma/Undergraduate degree.', 'Monthly stipend of INR 5,000 plus one-time incidentals grant of INR 6,000.', 'https://pminternship.mca.gov.in', '2026-10-31'),
(3, 'National Apprenticeship Promotion Scheme (NAPS)', 'Directorate General of Training / MSDE', 'APPRENTICESHIP', 'Promotes apprenticeship training by sharing stipend support with employers and providing on-the-job training.', 'Candidates aged 14 years and above having completed 5th/8th/10th/12th/ITI/Diploma.', 'Government co-shares 25% of prescribed stipend up to INR 1,500/month per apprentice.', 'https://www.apprenticeshipindia.gov.in', 'Ongoing'),
(4, 'Post-Matric Scholarship for Scheduled Castes (SC)', 'Ministry of Social Justice and Empowerment', 'SCHOLARSHIP', 'Centrally sponsored scholarship scheme to support SC students studying at post-matriculation or post-secondary stage.', 'SC students enrolled in recognized universities/colleges with annual family parental income not exceeding INR 2.5 Lakhs.', 'Full compulsory non-refundable fees reimbursement plus monthly maintenance allowance up to INR 13,500/year.', 'https://scholarships.gov.in', '2026-11-30'),
(5, 'AICTE Pragati Scholarship for Girl Students', 'All India Council for Technical Education (AICTE)', 'SCHOLARSHIP', 'Scheme aimed at providing assistance for advancement of girls pursuing technical education (degree/diploma).', 'Girl students admitted to 1st year of degree/diploma program with family income less than INR 8 Lakhs per annum.', 'INR 50,000 per annum for every year of study towards college fee, books, equipment, and laptop purchase.', 'https://facilities.aicte-india.org/pragati', '2026-11-15'),
(6, 'Digital India Internship Scheme', 'Ministry of Electronics and Information Technology (MeitY)', 'EMPLOYMENT_SCHEME', 'Short-term summer/winter internships giving students insight into governance processes and digital transformation.', 'Indian students studying B.Tech/M.Tech/MCA/M.Sc (IT) with minimum 60% marks in previous examinations.', 'Hands-on mentorship by MeitY officers and stipend of INR 10,000/month plus completion certificate.', 'https://meity.gov.in/internship', '2026-11-20'),
(7, 'Deen Dayal Upadhyaya Grameen Kaushalya Yojana (DDU-GKY)', 'Ministry of Rural Development', 'SKILL_DEVELOPMENT', 'Demand-driven placement-linked skilling initiative dedicated to rural youth from poor families.', 'Rural youth aged between 15 and 35 years (up to 45 years for women, PwD, and special groups).', 'Completely free residential training, food, transport, post-placement support, and guaranteed minimum 70% placement.', 'https://ddugky.gov.in', 'Ongoing'),
(8, 'National Overseas Scholarship Scheme', 'Ministry of Social Justice and Empowerment', 'SCHOLARSHIP', 'Facilitates low income students belonging to SC, de-notified nomadic tribes, and traditional artisans to obtain higher education abroad.', 'SC/ST candidates with minimum 60% marks in qualifying degree and family income below INR 8 Lakhs/year.', 'Total tuition fees, annual maintenance allowance (USD 15,400), contingency allowance, visa fees, and return airfare.', 'https://nosmsje.gov.in', '2026-12-31'),
(9, 'Samarth Scheme for Capacity Building in Textile Sector', 'Ministry of Textiles', 'SKILL_DEVELOPMENT', 'Skilling program aimed at providing placement-oriented training in traditional and modern textile manufacturing.', 'Indian citizens aged 18+ interested in technical spinning, weaving, garment manufacturing, and digital design.', 'Government certified NSQF training with guaranteed wage employment for at least 70% of certified trainees.', 'https://samarth-textiles.gov.in', 'Ongoing'),
(10, 'FutureSkills Prime (MeitY - NASSCOM)', 'Ministry of Electronics and IT & NASSCOM', 'SKILL_DEVELOPMENT', 'National reskilling and upskilling ecosystem in 10 emerging technologies including AI, IoT, Cloud, and Big Data.', 'Undergraduates, fresh engineers, and IT professionals seeking certification in deep tech.', 'Government subsidized certification, diagnostic assessment, and placement opportunities on national career portal.', 'https://futureskillsprime.in', 'Ongoing'),
(11, 'MahaDBT Post-Matric Scholarship Scheme', 'Government of Maharashtra', 'SCHOLARSHIP', 'Consolidated portal for state-specific educational scholarships and fee reimbursements for Maharashtra students.', 'Domicile of Maharashtra belonging to SC/ST/OBC/VJNT/SBC/EWS categories pursuing higher education.', 'Tuition fee concession, exam fee reimbursement, and maintenance allowance directly transferred via DBT.', 'https://mahadbt.maharashtra.gov.in', '2026-11-30'),
(12, 'National Apprenticeship Training Scheme (NATS)', 'Board of Practical Training / Ministry of Education', 'APPRENTICESHIP', 'Institutional bridge providing technical graduates and diploma holders practical on-the-job industrial skills.', 'Fresh graduates and diploma holders in Engineering/Technology and general streams passing within last 3 years.', 'Central government guaranteed monthly stipend directly credited via DBT, recognized certificate of proficiency.', 'https://nats.education.gov.in', 'Ongoing');
