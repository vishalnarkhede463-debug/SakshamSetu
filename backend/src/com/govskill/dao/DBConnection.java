package com.govskill.dao;

import com.govskill.models.*;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DBConnection {

    private static String dbUrl = "jdbc:mysql://localhost:3306/govskill_connect?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPass = "root";

    private static boolean mySqlAvailable = false;
    private static boolean checked = false;

    // In-memory data store for fallback when MySQL service is not running locally
    public static final InMemoryStore store = new InMemoryStore();

    static {
        loadConfig();
        checkMySqlAvailability();
    }

    private static void loadConfig() {
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                dbUrl = props.getProperty("db.url", dbUrl);
                dbUser = props.getProperty("db.user", dbUser);
                dbPass = props.getProperty("db.password", dbPass);
            }
        } catch (Exception ignored) {}

        // Environment variable overrides
        String envUrl = System.getenv("GOVSKILL_DB_URL");
        if (envUrl != null) dbUrl = envUrl;
        String envUser = System.getenv("GOVSKILL_DB_USER");
        if (envUser != null) dbUser = envUser;
        String envPass = System.getenv("GOVSKILL_DB_PASS");
        if (envPass != null) dbPass = envPass;
    }

    public static synchronized boolean checkMySqlAvailability() {
        if (checked) return mySqlAvailable;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                mySqlAvailable = (conn != null && !conn.isClosed());
                System.out.println("[DBConnection] Connected to MySQL successfully at " + dbUrl);
            }
        } catch (Throwable t) {
            mySqlAvailable = false;
            System.out.println("[DBConnection] MySQL is not active or driver not present. Initializing In-Memory Relational Engine with full seed dataset.");
        }
        checked = true;
        return mySqlAvailable;
    }

    public static Connection getConnection() throws SQLException {
        if (!mySqlAvailable) {
            return null;
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    public static boolean isMySqlAvailable() {
        return mySqlAvailable;
    }

    // -------------------------------------------------------------------------
    // Fallback In-Memory Engine with Full Production Seed Dataset
    // -------------------------------------------------------------------------
    public static class InMemoryStore {
        public final Map<Integer, User> users = new ConcurrentHashMap<>();
        public final Map<Integer, StudentProfile> studentProfiles = new ConcurrentHashMap<>();
        public final Map<Integer, Education> educations = new ConcurrentHashMap<>();
        public final Map<Integer, Skill> skills = new ConcurrentHashMap<>();
        public final Map<Integer, Certificate> certificates = new ConcurrentHashMap<>();
        public final Map<Integer, EmployerProfile> employerProfiles = new ConcurrentHashMap<>();
        public final Map<Integer, Opportunity> opportunities = new ConcurrentHashMap<>();
        public final Map<Integer, Application> applications = new ConcurrentHashMap<>();
        public final Set<String> savedOpportunities = Collections.newSetFromMap(new ConcurrentHashMap<>());
        public final Map<Integer, Notification> notifications = new ConcurrentHashMap<>();
        public final Map<Integer, GovernmentProgram> governmentPrograms = new ConcurrentHashMap<>();

        public final AtomicInteger userIdSeq = new AtomicInteger(100);
        public final AtomicInteger studentIdSeq = new AtomicInteger(100);
        public final AtomicInteger eduIdSeq = new AtomicInteger(100);
        public final AtomicInteger skillIdSeq = new AtomicInteger(100);
        public final AtomicInteger certIdSeq = new AtomicInteger(100);
        public final AtomicInteger empIdSeq = new AtomicInteger(100);
        public final AtomicInteger oppIdSeq = new AtomicInteger(100);
        public final AtomicInteger appIdSeq = new AtomicInteger(100);
        public final AtomicInteger notifIdSeq = new AtomicInteger(100);
        public final AtomicInteger progIdSeq = new AtomicInteger(100);

        public InMemoryStore() {
            seed();
        }

        private void seed() {
            // Admin user
            users.put(1, new User(1, "National Portal Administrator", "admin@govskill.gov.in", "9811001122", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "ADMIN", "Delhi", "New Delhi"));

            // Employers
            users.put(2, new User(2, "ISRO Recruitment Cell", "hr@isro.gov.in", "9822001122", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "EMPLOYER", "Karnataka", "Bengaluru"));
            users.put(3, new User(3, "BHEL Apprenticeship Division", "hr@bhel.in", "9833001122", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "EMPLOYER", "Delhi", "New Delhi"));
            users.put(4, new User(4, "Tata Consultancy Services", "careers@tcs.com", "9844001122", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "EMPLOYER", "Maharashtra", "Mumbai"));
            users.put(5, new User(5, "Infosys Springboard Talent", "talent@infosys.com", "9855001122", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "EMPLOYER", "Karnataka", "Bengaluru"));
            users.put(6, new User(6, "Maharashtra State Innovation Society", "info@msins.gov.in", "9866001122", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "EMPLOYER", "Maharashtra", "Pune"));

            // Employer Profiles
            EmployerProfile ep1 = new EmployerProfile();
            ep1.setId(1); ep1.setUserId(2); ep1.setOrganizationName("Indian Space Research Organisation (ISRO)");
            ep1.setOrganizationType("GOVERNMENT"); ep1.setDescription("Primary space exploration agency of India.");
            ep1.setWebsite("https://www.isro.gov.in"); ep1.setVerificationStatus("VERIFIED");
            employerProfiles.put(1, ep1);

            EmployerProfile ep2 = new EmployerProfile();
            ep2.setId(2); ep2.setUserId(3); ep2.setOrganizationName("Bharat Heavy Electricals Limited (BHEL)");
            ep2.setOrganizationType("PSU"); ep2.setDescription("India's largest engineering enterprise.");
            ep2.setWebsite("https://www.bhel.in"); ep2.setVerificationStatus("VERIFIED");
            employerProfiles.put(2, ep2);

            EmployerProfile ep3 = new EmployerProfile();
            ep3.setId(3); ep3.setUserId(4); ep3.setOrganizationName("Tata Consultancy Services Ltd");
            ep3.setOrganizationType("PRIVATE"); ep3.setDescription("Global leader in IT consulting and services.");
            ep3.setWebsite("https://www.tcs.com"); ep3.setVerificationStatus("VERIFIED");
            employerProfiles.put(3, ep3);

            EmployerProfile ep4 = new EmployerProfile();
            ep4.setId(4); ep4.setUserId(5); ep4.setOrganizationName("Infosys Springboard Division");
            ep4.setOrganizationType("PRIVATE"); ep4.setDescription("Pioneering enterprise software and training.");
            ep4.setWebsite("https://www.infosys.com"); ep4.setVerificationStatus("VERIFIED");
            employerProfiles.put(4, ep4);

            EmployerProfile ep5 = new EmployerProfile();
            ep5.setId(5); ep5.setUserId(6); ep5.setOrganizationName("Maharashtra State Innovation Society");
            ep5.setOrganizationType("GOVERNMENT"); ep5.setDescription("Nodal startup promoting body.");
            ep5.setWebsite("https://msins.in"); ep5.setVerificationStatus("VERIFIED");
            employerProfiles.put(5, ep5);

            // Students
            addStudent(7, "Rahul Sharma", "rahul.sharma@gmail.com", "9911223344", "Maharashtra", "Pune",
                    1, "2002-05-14", "MALE", "OBC", "Computer Science & Engineering", "Undergraduate", "College of Engineering Pune (COEP)", 2024,
                    "Passionate software developer interested in Java, C++, and Distributed Systems.", true, true, 90);

            addStudent(8, "Priya Patil", "priya.patil@gmail.com", "9922334455", "Maharashtra", "Mumbai",
                    2, "2003-08-22", "FEMALE", "GENERAL", "Information Technology", "Undergraduate", "Veermata Jijabai Technological Institute (VJTI)", 2025,
                    "Full stack web developer and cloud enthusiast aiming for research opportunities.", true, true, 85);

            addStudent(9, "Aniket Deshmukh", "aniket.deshmukh@gmail.com", "9933445566", "Maharashtra", "Nagpur",
                    3, "2001-11-10", "MALE", "SC", "Mechanical Engineering", "Undergraduate", "VNIT Nagpur", 2023,
                    "CAD modeling, thermal analysis and robotics automation specialist.", true, true, 80);

            addStudent(10, "Sneha Kulkarni", "sneha.kulkarni@gmail.com", "9944556677", "Maharashtra", "Nashik",
                    4, "2002-03-18", "FEMALE", "OBC", "Electronics & Telecommunication", "Undergraduate", "K.K. Wagh Institute of Engineering", 2024,
                    "IoT, Embedded Systems, and Signal Processing enthusiast.", true, true, 75);

            addStudent(11, "Amit Verma", "amit.verma@gmail.com", "9955667788", "Delhi", "Delhi",
                    5, "2000-09-05", "MALE", "GENERAL", "Computer Science", "Postgraduate", "Delhi Technological University (DTU)", 2023,
                    "Machine learning researcher and data science practitioner.", true, true, 95);

            addStudent(12, "Neha Singh", "neha.singh@gmail.com", "9966778899", "Karnataka", "Bengaluru",
                    6, "2003-01-25", "FEMALE", "EWS", "Data Science & AI", "Undergraduate", "BMS College of Engineering", 2025,
                    "Python and Data Analytics learner looking for internships.", true, true, 70);

            addStudent(13, "Rohit Jadhav", "rohit.jadhav@gmail.com", "9977889900", "Maharashtra", "Thane",
                    7, "2002-07-12", "MALE", "GENERAL", "Electrical Engineering", "Diploma", "Government Polytechnic Thane", 2023,
                    "Power systems, industrial wiring, and PLC automation.", false, false, 65); // Consent OFF for testing

            addStudent(14, "Pooja Shinde", "pooja.shinde@gmail.com", "9988990011", "Maharashtra", "Navi Mumbai",
                    8, "2001-12-30", "FEMALE", "ST", "Civil Engineering", "Undergraduate", "DY Patil College of Engineering", 2024,
                    "Structural planning, AutoCAD, and green building projects.", true, true, 85);

            addStudent(15, "Vikram Nair", "vikram.nair@gmail.com", "9900112233", "Telangana", "Hyderabad",
                    9, "2002-04-16", "MALE", "GENERAL", "Computer Science & Engineering", "Undergraduate", "JNTU Hyderabad", 2024,
                    "Cyber security, network defense, and ethical hacking.", true, true, 90);

            addStudent(16, "Kavita Joshi", "kavita.joshi@gmail.com", "9912345678", "Maharashtra", "Pune",
                    10, "2003-10-08", "FEMALE", "OBC", "Information Technology", "Undergraduate", "MIT World Peace University", 2025,
                    "Web development and digital marketing enthusiast.", true, true, 80);

            // Education
            educations.put(1, new Education(1, 1, "Undergraduate", "College of Engineering Pune (COEP)", "Computer Science & Engineering", 88.5, 2024));
            educations.put(2, new Education(2, 1, "12th", "Modern Junior College Pune", "Science", 91.2, 2020));
            educations.put(3, new Education(3, 2, "Undergraduate", "Veermata Jijabai Technological Institute (VJTI)", "Information Technology", 86.4, 2025));
            educations.put(4, new Education(4, 3, "Undergraduate", "VNIT Nagpur", "Mechanical Engineering", 82.1, 2023));
            educations.put(5, new Education(5, 4, "Undergraduate", "K.K. Wagh Institute of Engineering", "Electronics & Telecommunication", 84.7, 2024));
            educations.put(6, new Education(6, 5, "Postgraduate", "Delhi Technological University (DTU)", "Computer Science", 89.0, 2023));
            educations.put(7, new Education(7, 6, "Undergraduate", "BMS College of Engineering", "Data Science & AI", 81.5, 2025));
            educations.put(8, new Education(8, 7, "Diploma", "Government Polytechnic Thane", "Electrical Engineering", 79.3, 2023));
            educations.put(9, new Education(9, 8, "Undergraduate", "DY Patil College of Engineering", "Civil Engineering", 83.2, 2024));
            educations.put(10, new Education(10, 9, "Undergraduate", "JNTU Hyderabad", "Computer Science & Engineering", 87.6, 2024));
            educations.put(11, new Education(11, 10, "Undergraduate", "MIT World Peace University", "Information Technology", 85.0, 2025));

            // Skills
            skills.put(1, new Skill(1, 1, "Java", "ADVANCED", true));
            skills.put(2, new Skill(2, 1, "C++", "ADVANCED", true));
            skills.put(3, new Skill(3, 1, "SQL", "ADVANCED", true));
            skills.put(4, new Skill(4, 1, "Web Development", "INTERMEDIATE", false));
            skills.put(5, new Skill(5, 2, "JavaScript", "ADVANCED", true));
            skills.put(6, new Skill(6, 2, "HTML", "EXPERT", true));
            skills.put(7, new Skill(7, 2, "CSS", "EXPERT", true));
            skills.put(8, new Skill(8, 2, "Python", "INTERMEDIATE", false));
            skills.put(9, new Skill(9, 3, "AutoCAD", "ADVANCED", true));
            skills.put(10, new Skill(10, 3, "Communication", "ADVANCED", true));
            skills.put(11, new Skill(11, 4, "C", "ADVANCED", true));
            skills.put(12, new Skill(12, 4, "Cyber Security", "BEGINNER", false));
            skills.put(13, new Skill(13, 5, "Python", "EXPERT", true));
            skills.put(14, new Skill(14, 5, "AI/ML", "EXPERT", true));
            skills.put(15, new Skill(15, 5, "Data Analytics", "ADVANCED", true));
            skills.put(16, new Skill(16, 6, "Data Analytics", "INTERMEDIATE", false));
            skills.put(17, new Skill(17, 7, "PLC Automation", "INTERMEDIATE", false));
            skills.put(18, new Skill(18, 8, "Civil Engineering", "ADVANCED", true));
            skills.put(19, new Skill(19, 9, "Cyber Security", "ADVANCED", true));
            skills.put(20, new Skill(20, 9, "Java", "INTERMEDIATE", true));
            skills.put(21, new Skill(21, 10, "Digital Marketing", "INTERMEDIATE", true));
            skills.put(22, new Skill(22, 10, "Web Development", "INTERMEDIATE", false));

            // Certificates
            certificates.put(1, new Certificate(1, 1, 1, "Oracle Certified Professional: Java SE 11", "Oracle University", "/uploads/cert_oracle_java.pdf", "2023-06-15"));
            certificates.put(2, new Certificate(2, 1, 2, "C++ High Performance Certificate", "NPTEL / IIT Bombay", "/uploads/cert_nptel_cpp.pdf", "2023-09-20"));
            certificates.put(3, new Certificate(3, 1, 3, "Relational Database Management Systems with SQL", "IIT Kharagpur", "/uploads/cert_dbms_sql.pdf", "2023-11-10"));
            certificates.put(4, new Certificate(4, 2, 5, "Modern JavaScript Fundamentals", "FreeCodeCamp", "/uploads/cert_fcc_js.pdf", "2024-02-14"));
            certificates.put(5, new Certificate(5, 2, 6, "Responsive Web Design Certification", "W3C / edX", "/uploads/cert_w3c_html.pdf", "2024-01-10"));
            certificates.put(6, new Certificate(6, 3, 9, "Certified SolidWorks Associate", "Dassault Systèmes", "/uploads/cert_solidworks.pdf", "2022-12-05"));
            certificates.put(7, new Certificate(7, 4, 11, "Embedded C and Microcontroller Programming", "CDAC Pune", "/uploads/cert_cdac_c.pdf", "2023-08-18"));
            certificates.put(8, new Certificate(8, 5, 13, "Python for Data Science and Machine Learning", "IIT Madras", "/uploads/cert_nptel_python.pdf", "2023-05-12"));
            certificates.put(9, new Certificate(9, 5, 14, "Deep Learning Specialization Certificate", "DeepLearning.AI", "/uploads/cert_dl_specialization.pdf", "2023-07-28"));
            certificates.put(10, new Certificate(10, 9, 19, "Certified Ethical Hacker (CEH v12)", "EC-Council", "/uploads/cert_ceh.pdf", "2024-03-01"));

            // Opportunities (25 records)
            addOpportunity(1, 1, "Junior Scientist / Engineer (Computer Science)",
                    "Recruitment for ICRB Engineer positions to support satellite data ground stations and telemetry networks.",
                    "GOVERNMENT_JOB", "ISRO - Satellite Centre", "Bengaluru", "Karnataka", "Bengaluru",
                    "Computer Science & Engineering", "B.Tech / B.E.", "Java, C++, Data Structures, Algorithms, Linux",
                    "GENERAL", "INR 56,100 - 1,77,500 / month", "2026-11-30", "https://www.isro.gov.in/careers", true);

            addOpportunity(2, 2, "Graduate Apprenticeship Trainee (Mechanical)",
                    "One year National Apprenticeship Training (NATS) for fresh engineering graduates in turbine manufacturing.",
                    "APPRENTICESHIP", "Bharat Heavy Electricals Limited (BHEL)", "New Delhi", "Delhi", "New Delhi",
                    "Mechanical Engineering", "B.Tech / B.E. / Diploma", "AutoCAD, Thermal Engineering, Manufacturing Processes",
                    "ALL", "INR 15,000 / month stipend", "2026-10-15", "https://www.bhel.in/apprenticeship", true);

            addOpportunity(3, 3, "Systems Engineer Trainee",
                    "Software development, testing, and deployment for enterprise banking and government digital applications.",
                    "PRIVATE_JOB", "Tata Consultancy Services", "Pune", "Maharashtra", "Pune",
                    "Computer Science, IT, Electronics", "B.Tech / B.E. / MCA", "Java, SQL, JavaScript, HTML, CSS",
                    "ALL", "INR 4.5 LPA - 7.0 LPA", "2026-12-15", "https://www.tcs.com/careers", true);

            addOpportunity(4, 5, "Prime Minister Internship Scheme 2026",
                    "Government-backed 12-month internship in leading enterprise setups offering industrial exposure and monthly financial assistance.",
                    "INTERNSHIP", "Ministry of Corporate Affairs / MSInS", "Mumbai", "Maharashtra", "Mumbai",
                    "All Engineering & Technology Branches", "Diploma / Undergraduate", "Communication, Web Development, Digital Marketing, MS Office",
                    "ALL", "INR 5,000 / month stipend + INR 6,000 grant", "2026-10-31", "https://pminternship.mca.gov.in", true);

            addOpportunity(5, 1, "Technical Assistant (Electronics & Communication)",
                    "Operation, maintenance, and testing of high-frequency telemetry receivers and space payload testing rigs.",
                    "GOVERNMENT_JOB", "ISRO - Space Applications Centre", "Bengaluru", "Karnataka", "Bengaluru",
                    "Electronics & Telecommunication", "Diploma in Engineering", "C, Embedded Systems, Microcontrollers, Circuit Design",
                    "OBC", "INR 44,900 - 1,42,400 / month", "2026-11-20", "https://www.isro.gov.in/sac", true);

            addOpportunity(6, 4, "Data Analytics Intern",
                    "Work closely with data engineers to build automated pipelines, visualize telemetry data, and clean datasets.",
                    "INTERNSHIP", "Infosys Springboard", "Bengaluru", "Karnataka", "Bengaluru",
                    "Computer Science, Data Science, IT", "Undergraduate", "Python, Data Analytics, SQL, Statistics",
                    "ALL", "INR 25,000 / month stipend", "2026-10-28", "https://springboard.infosys.com", true);

            addOpportunity(7, 2, "Technician Apprentice (Electrical)",
                    "Shop floor maintenance of high voltage transformers, switchgear, and auxiliary electrical systems under NAPS.",
                    "APPRENTICESHIP", "BHEL Electrical Plant", "Nagpur", "Maharashtra", "Nagpur",
                    "Electrical Engineering", "Diploma / ITI", "PLC Automation, Electrical Wiring, Safety Compliance",
                    "SC", "INR 12,500 / month stipend", "2026-10-25", "https://apprenticeshipindia.gov.in", true);

            addOpportunity(8, null, "National Post-Matric Scholarship for Higher Education",
                    "Financial aid for meritorious students from socio-economically weaker backgrounds pursuing degree programs.",
                    "SCHOLARSHIP", "Ministry of Social Justice & Empowerment", "New Delhi", "Delhi", "New Delhi",
                    "Any Branch", "12th / Diploma / Undergraduate", "Academic Merit, Minimum 60% in previous exam",
                    "SC", "INR 1,20,000 / year + tuition waiver", "2026-11-15", "https://scholarships.gov.in", true);

            addOpportunity(9, null, "PMKVY 4.0: Certified Cyber Security Associate",
                    "Pradhan Mantri Kaushal Vikas Yojana funded 400-hour skill development course with 100% placement support.",
                    "SKILL_DEVELOPMENT", "National Skill Development Corporation (NSDC)", "Pune", "Maharashtra", "Pune",
                    "Any Technical Branch", "12th / Diploma / Degree", "Cyber Security, Networking, Linux, Firewalls",
                    "ALL", "Free Course + Government Certificate + INR 8,000 allowance", "2026-11-10", "https://www.skillindiadigital.gov.in", true);

            addOpportunity(10, 5, "AI & Machine Learning Research Intern",
                    "Hands-on exposure to NLP, LLM fine-tuning, and computer vision models for government civic chatbots.",
                    "INTERNSHIP", "Maharashtra State Innovation Society", "Pune", "Maharashtra", "Pune",
                    "Computer Science, AI, Mathematics", "B.Tech / M.Tech / MCA", "Python, AI/ML, Data Analytics, Deep Learning",
                    "ALL", "INR 30,000 / month stipend", "2026-10-30", "https://msins.in/fellowship", true);

            addOpportunity(11, 3, "Junior Web Developer (Frontend)",
                    "Develop accessible, multilingual citizen service portal interfaces adhering to GIGW standards.",
                    "PRIVATE_JOB", "Tata Consultancy Services", "Thane", "Maharashtra", "Thane",
                    "Computer Science, IT", "BCA / B.Sc / B.Tech", "HTML, CSS, JavaScript, Web Development",
                    "ALL", "INR 3.8 LPA - 5.5 LPA", "2026-12-05", "https://www.tcs.com/careers", true);

            addOpportunity(12, null, "Pragati Scholarship Scheme for Girl Students",
                    "AICTE financial scholarship to empower girl students admitted into technical degree and diploma courses.",
                    "SCHOLARSHIP", "AICTE / Ministry of Education", "New Delhi", "Delhi", "New Delhi",
                    "Engineering / Technology", "First Year Degree / Diploma", "Minimum 60% in 12th / 10th",
                    "FEMALE", "INR 50,000 / annum", "2026-11-30", "https://facilities.aicte-india.org/pragati", true);

            addOpportunity(13, null, "Apprentice Engineer (Civil & Urban Infrastructure)",
                    "Site supervision, quality verification, and structural inspection for smart city transportation corridors.",
                    "APPRENTICESHIP", "National Highways Authority of India (NHAI)", "Nashik", "Maharashtra", "Nashik",
                    "Civil Engineering", "B.Tech / Diploma in Civil", "AutoCAD, Structural Design, Surveying",
                    "ALL", "INR 16,000 / month stipend", "2026-10-20", "https://nhai.gov.in", true);

            addOpportunity(14, 4, "Cyber Security Analyst Trainee",
                    "Threat monitoring, vulnerability assessment, and log auditing in Security Operations Center (SOC).",
                    "PRIVATE_JOB", "Infosys Cyber Defense Center", "Hyderabad", "Telangana", "Hyderabad",
                    "Computer Science, IT, Cyber Security", "B.Tech / B.E.", "Cyber Security, Python, Networking, Linux",
                    "ALL", "INR 5.0 LPA - 8.0 LPA", "2026-12-20", "https://www.infosys.com/security", true);

            addOpportunity(15, null, "PMKVY 4.0: Full Stack Web Developer Certification",
                    "Government sponsored vocational training curriculum covering HTML, CSS, JavaScript, Java, and Database systems.",
                    "SKILL_DEVELOPMENT", "Skill India Digital Hub", "Navi Mumbai", "Maharashtra", "Navi Mumbai",
                    "Any Stream", "10th / 12th Pass", "HTML, CSS, JavaScript, Web Development",
                    "ALL", "Free Training + Govt NSQF Level 5 Certification", "2026-11-05", "https://www.skillindiadigital.gov.in", true);

            addOpportunity(16, 1, "Junior Research Fellow (Propulsion Systems)",
                    "Computational fluid dynamics and propulsion simulation for next generation satellite launch vehicles.",
                    "GOVERNMENT_JOB", "DRDO - Defence Research Laboratory", "Pune", "Maharashtra", "Pune",
                    "Mechanical Engineering, Aerospace", "B.E. / B.Tech (First Class)", "C++, MATLAB, CFD, Thermal Engineering",
                    "GENERAL", "INR 37,000 / month + HRA", "2026-11-15", "https://www.drdo.gov.in/careers", true);

            addOpportunity(17, 3, "Java Enterprise Software Engineer",
                    "Backend development for mission critical railway freight and passenger reservation modules.",
                    "PRIVATE_JOB", "TCS Government Projects Business Unit", "Navi Mumbai", "Maharashtra", "Navi Mumbai",
                    "Computer Science, IT", "B.Tech / MCA", "Java, SQL, C++, Spring Boot, Linux",
                    "ALL", "INR 6.5 LPA - 9.5 LPA", "2026-12-22", "https://www.tcs.com/careers", true);

            addOpportunity(18, null, "National Overseas Scholarship for SC / ST Candidates",
                    "Financial assistance to students selected for Masters and Ph.D. programs in top-ranked overseas universities.",
                    "SCHOLARSHIP", "Ministry of Tribal Affairs & Social Justice", "New Delhi", "Delhi", "New Delhi",
                    "Engineering, Pure Sciences, Medicine", "Undergraduate / Postgraduate", "Academic Excellence (>60%), University Admit Letter",
                    "SC", "Full Tuition + Living Allowance (USD 15,400/yr)", "2026-12-31", "https://nosmsje.gov.in", true);

            addOpportunity(19, 2, "ITI Trade Apprentice (Fitter / Electrician)",
                    "Practical hands-on training under the Apprentices Act 1961 inside precision manufacturing bays.",
                    "APPRENTICESHIP", "BHEL Manufacturing Complex", "Hyderabad", "Telangana", "Hyderabad",
                    "Mechanical / Electrical", "ITI Passed in Relevant Trade", "Trade Theory, Machine Shop Operations, Electrical Wiring",
                    "OBC", "INR 10,500 / month stipend", "2026-10-18", "https://www.bhel.in/careers", true);

            addOpportunity(20, null, "Digital India FutureSkills Prime: AI Data Annotation",
                    "Certification program for young graduates to master generative AI data labelling, annotation, and prompt testing.",
                    "SKILL_DEVELOPMENT", "Ministry of Electronics and IT (MeitY) / NASSCOM", "Delhi", "Delhi", "New Delhi",
                    "Any Graduate", "Degree / Diploma Completed", "Data Analytics, Communication, Basic Computer Literacy",
                    "ALL", "Subsidized Fee + NSDC Certification + Job Fair Access", "2026-11-25", "https://futureskillsprime.in", true);

            addOpportunity(21, 5, "Junior Software Associate",
                    "Entry level software engineering position focusing on cloud migration and database optimization.",
                    "PRIVATE_JOB", "Infosys Technology Labs", "Pune", "Maharashtra", "Pune",
                    "Computer Science, IT", "B.Sc (IT) / BCA / B.Tech", "Java, Python, SQL, Git",
                    "ALL", "INR 4.0 LPA - 6.0 LPA", "2026-11-28", "https://www.infosys.com/careers", true);

            addOpportunity(22, 1, "Scientific Officer / Technical Lead",
                    "Satellite navigation and atomic clock frequency standard calibration.",
                    "GOVERNMENT_JOB", "National Physical Laboratory (CSIR-NPL)", "Delhi", "Delhi", "New Delhi",
                    "Electronics, Applied Physics", "M.Sc / M.Tech", "C, C++, Signal Processing, Instrumentation",
                    "ALL", "INR 67,700 - 2,08,700 / month", "2026-12-10", "https://www.nplindia.in", true);

            addOpportunity(23, null, "Post-Matric Scholarship for OBC Students",
                    "Centrally sponsored scheme providing financial stipend to OBC students pursuing post-matriculation courses.",
                    "SCHOLARSHIP", "Department of Social Justice", "Mumbai", "Maharashtra", "Mumbai",
                    "Any Stream", "10th / 12th / Diploma / Degree", "OBC Certificate, Annual Family Income < INR 2.5 Lakhs",
                    "OBC", "INR 25,000 to 50,000 / year", "2026-11-30", "https://mahadbt.maharashtra.gov.in", true);

            addOpportunity(24, null, "DDU-GKY Rural Youth Skill Training (Solar PV Technician)",
                    "Deen Dayal Upadhyaya Grameen Kaushalya Yojana residential skilling program for rural youth with placement guarantee.",
                    "SKILL_DEVELOPMENT", "Ministry of Rural Development", "Nagpur", "Maharashtra", "Nagpur",
                    "Electrical / Renewable Energy", "10th / 12th Pass", "Solar Installation, Electrical Wiring, Maintenance",
                    "ALL", "Free Residential Training + Uniform + Placed Employment", "2026-11-12", "https://ddugky.gov.in", true);

            addOpportunity(25, 4, "Graduate Trainee - Business Systems",
                    "Requirements gathering, client coordination, and digital reporting for state e-governance solutions.",
                    "PRIVATE_JOB", "Tata Consultancy Services", "Hyderabad", "Telangana", "Hyderabad",
                    "All Engineering Disciplines", "B.Tech / B.E.", "Communication, Digital Marketing, MS Excel, SQL",
                    "ALL", "INR 4.2 LPA - 5.8 LPA", "2026-12-18", "https://www.tcs.com/careers", true);

            // Applications (22 records)
            addApplication(1, 1, 1, "SHORTLISTED", "COEP CSE graduate, Oracle Certified Java developer.", "2026-08-10");
            addApplication(2, 3, 1, "SELECTED", "Strong coding skills in Java and SQL.", "2026-08-12");
            addApplication(3, 4, 1, "APPLIED", "Interested in PM Internship Scheme industrial exposure.", "2026-08-15");
            addApplication(4, 17, 1, "UNDER_REVIEW", "Experienced in backend Java development and database systems.", "2026-08-18");
            addApplication(5, 3, 2, "SHORTLISTED", "VJTI IT undergraduate skilled in JavaScript, HTML, and modern web frameworks.", "2026-08-11");
            addApplication(6, 4, 2, "SELECTED", "Interested in state-level digital e-governance initiatives.", "2026-08-14");
            addApplication(7, 11, 2, "UNDER_REVIEW", "Applying for Junior Web Developer position.", "2026-08-20");
            addApplication(8, 2, 3, "SHORTLISTED", "B.Tech Mechanical from VNIT Nagpur. Certified in SolidWorks.", "2026-08-05");
            addApplication(9, 7, 3, "APPLIED", "Application for Apprenticeship in electrical and manufacturing maintenance.", "2026-08-08");
            addApplication(10, 5, 4, "UNDER_REVIEW", "E&TC undergraduate with practical embedded C and microcontroller skills.", "2026-08-16");
            addApplication(11, 16, 4, "APPLIED", "Interested in defense communication engineering.", "2026-08-22");
            addApplication(12, 6, 5, "SELECTED", "M.Tech CSE with publications in machine learning.", "2026-08-02");
            addApplication(13, 10, 5, "SHORTLISTED", "Applying for AI & ML Research Intern. Hands-on experience in PyTorch.", "2026-08-09");
            addApplication(14, 14, 5, "UNDER_REVIEW", "Strong background in algorithms, Python, and data analytics.", "2026-08-19");
            addApplication(15, 6, 6, "APPLIED", "Data science undergraduate keen on learning enterprise data analytics.", "2026-08-21");
            addApplication(16, 15, 6, "APPLIED", "Enrolling in skill development for full stack web development.", "2026-08-23");
            addApplication(17, 7, 7, "REJECTED", "Diploma electrical passed. Applied for BHEL apprentice.", "2026-08-01");
            addApplication(18, 13, 8, "SHORTLISTED", "Civil engineering graduate with strong structural planning skills.", "2026-08-13");
            addApplication(19, 14, 9, "SHORTLISTED", "Certified Ethical Hacker with hands-on lab experience.", "2026-08-07");
            addApplication(20, 1, 9, "UNDER_REVIEW", "Applying for ISRO Engineer position in computer security.", "2026-08-17");
            addApplication(21, 11, 10, "APPLIED", "IT undergraduate interested in digital communication.", "2026-08-24");
            addApplication(22, 25, 10, "SHORTLISTED", "Skilled in digital marketing, presentation, and data management.", "2026-08-25");

            // Saved Opportunities
            savedOpportunities.add("1_1");
            savedOpportunities.add("1_6");
            savedOpportunities.add("1_10");
            savedOpportunities.add("2_11");
            savedOpportunities.add("2_4");
            savedOpportunities.add("3_2");
            savedOpportunities.add("5_1");
            savedOpportunities.add("9_14");

            // Notifications
            notifications.put(1, new Notification(1, 7, "Application Shortlisted", "Congratulations! Your application for Junior Scientist / Engineer at ISRO has been shortlisted for technical interview.", false, "2026-09-05 10:30"));
            notifications.put(2, new Notification(2, 7, "Application Selected", "Great news! You have been selected for Systems Engineer Trainee at Tata Consultancy Services.", true, "2026-09-02 14:15"));
            notifications.put(3, new Notification(3, 7, "New Matching Opportunity", "A new Government Job matching your skills (Java, C++) was posted: Junior Research Fellow at DRDO.", true, "2026-09-01 09:00"));
            notifications.put(4, new Notification(4, 7, "Profile Completion Reminder", "Your profile is 90% complete. Add your latest project or certificate to reach 100%.", true, "2026-08-28 11:20"));
            notifications.put(5, new Notification(5, 8, "Application Selected", "You have been selected for the Prime Minister Internship Scheme 2026. Please review joining instructions.", false, "2026-09-06 16:45"));
            notifications.put(6, new Notification(6, 8, "Skill Verified", "Your skill \"JavaScript\" has been verified following document review.", true, "2026-09-03 12:00"));
            notifications.put(7, new Notification(7, 9, "Application Shortlisted", "Your application for Graduate Apprenticeship Trainee at BHEL has been shortlisted.", false, "2026-09-07 15:30"));
            notifications.put(8, new Notification(8, 11, "Application Selected", "You have been selected for the AI & Machine Learning Research Fellowship at MSInS.", false, "2026-09-04 11:10"));
            notifications.put(9, new Notification(9, 15, "Application Shortlisted", "Your application for Cyber Security Analyst Trainee at Infosys has progressed to technical review.", false, "2026-09-07 18:00"));

            // Government Programs (12 schemes)
            governmentPrograms.put(1, new GovernmentProgram(1, "Pradhan Mantri Kaushal Vikas Yojana (PMKVY 4.0)",
                    "Ministry of Skill Development and Entrepreneurship (MSDE)", "SKILL_DEVELOPMENT",
                    "Flagship scheme for imparting industry-relevant skill training to Indian youth to enable them to secure better livelihoods.",
                    "Any Indian citizen aged 15-45 years with basic education (10th/12th/ITI).",
                    "100% Free Training, Government NSQF certification, accident insurance, and direct placement support.",
                    "https://www.skillindiadigital.gov.in", "Ongoing - Cycle 2026"));

            governmentPrograms.put(2, new GovernmentProgram(2, "Prime Minister Internship Scheme (PMIS)",
                    "Ministry of Corporate Affairs", "EMPLOYMENT_SCHEME",
                    "Provides 12 months of real-world internship opportunity in top 500 companies across India.",
                    "Candidates aged 21-24 years not engaged in full-time employment, possessing ITI/Diploma/Undergraduate degree.",
                    "Monthly stipend of INR 5,000 plus one-time incidentals grant of INR 6,000.",
                    "https://pminternship.mca.gov.in", "2026-10-31"));

            governmentPrograms.put(3, new GovernmentProgram(3, "National Apprenticeship Promotion Scheme (NAPS)",
                    "Directorate General of Training / MSDE", "APPRENTICESHIP",
                    "Promotes apprenticeship training by sharing stipend support with employers and providing on-the-job training.",
                    "Candidates aged 14 years and above having completed 5th/8th/10th/12th/ITI/Diploma.",
                    "Government co-shares 25% of prescribed stipend up to INR 1,500/month per apprentice.",
                    "https://www.apprenticeshipindia.gov.in", "Ongoing"));

            governmentPrograms.put(4, new GovernmentProgram(4, "Post-Matric Scholarship for Scheduled Castes (SC)",
                    "Ministry of Social Justice and Empowerment", "SCHOLARSHIP",
                    "Centrally sponsored scholarship scheme to support SC students studying at post-matriculation or post-secondary stage.",
                    "SC students enrolled in recognized universities/colleges with annual family parental income not exceeding INR 2.5 Lakhs.",
                    "Full compulsory non-refundable fees reimbursement plus monthly maintenance allowance up to INR 13,500/year.",
                    "https://scholarships.gov.in", "2026-11-30"));

            governmentPrograms.put(5, new GovernmentProgram(5, "AICTE Pragati Scholarship for Girl Students",
                    "All India Council for Technical Education (AICTE)", "SCHOLARSHIP",
                    "Scheme aimed at providing assistance for advancement of girls pursuing technical education (degree/diploma).",
                    "Girl students admitted to 1st year of degree/diploma program with family income less than INR 8 Lakhs per annum.",
                    "INR 50,000 per annum for every year of study towards college fee, books, equipment, and laptop purchase.",
                    "https://facilities.aicte-india.org/pragati", "2026-11-15"));

            governmentPrograms.put(6, new GovernmentProgram(6, "Digital India Internship Scheme",
                    "Ministry of Electronics and Information Technology (MeitY)", "EMPLOYMENT_SCHEME",
                    "Short-term summer/winter internships giving students insight into governance processes and digital transformation.",
                    "Indian students studying B.Tech/M.Tech/MCA/M.Sc (IT) with minimum 60% marks in previous examinations.",
                    "Hands-on mentorship by MeitY officers and stipend of INR 10,000/month plus completion certificate.",
                    "https://meity.gov.in/internship", "2026-11-20"));

            governmentPrograms.put(7, new GovernmentProgram(7, "Deen Dayal Upadhyaya Grameen Kaushalya Yojana (DDU-GKY)",
                    "Ministry of Rural Development", "SKILL_DEVELOPMENT",
                    "Demand-driven placement-linked skilling initiative dedicated to rural youth from poor families.",
                    "Rural youth aged between 15 and 35 years (up to 45 years for women, PwD, and special groups).",
                    "Completely free residential training, food, transport, post-placement support, and guaranteed minimum 70% placement.",
                    "https://ddugky.gov.in", "Ongoing"));

            governmentPrograms.put(8, new GovernmentProgram(8, "National Overseas Scholarship Scheme",
                    "Ministry of Social Justice and Empowerment", "SCHOLARSHIP",
                    "Facilitates low income students belonging to SC, nomadic tribes, and artisans to obtain higher education abroad.",
                    "SC/ST candidates with minimum 60% marks in qualifying degree and family income below INR 8 Lakhs/year.",
                    "Total tuition fees, annual maintenance allowance (USD 15,400), contingency allowance, visa fees, and return airfare.",
                    "https://nosmsje.gov.in", "2026-12-31"));

            governmentPrograms.put(9, new GovernmentProgram(9, "Samarth Scheme for Capacity Building in Textile Sector",
                    "Ministry of Textiles", "SKILL_DEVELOPMENT",
                    "Skilling program aimed at providing placement-oriented training in traditional and modern textile manufacturing.",
                    "Indian citizens aged 18+ interested in technical spinning, weaving, garment manufacturing, and digital design.",
                    "Government certified NSQF training with guaranteed wage employment for at least 70% of certified trainees.",
                    "https://samarth-textiles.gov.in", "Ongoing"));

            governmentPrograms.put(10, new GovernmentProgram(10, "FutureSkills Prime (MeitY - NASSCOM)",
                    "Ministry of Electronics and IT & NASSCOM", "SKILL_DEVELOPMENT",
                    "National reskilling and upskilling ecosystem in 10 emerging technologies including AI, IoT, Cloud, and Big Data.",
                    "Undergraduates, fresh engineers, and IT professionals seeking certification in deep tech.",
                    "Government subsidized certification, diagnostic assessment, and placement opportunities on national career portal.",
                    "https://futureskillsprime.in", "Ongoing"));

            governmentPrograms.put(11, new GovernmentProgram(11, "MahaDBT Post-Matric Scholarship Scheme",
                    "Government of Maharashtra", "SCHOLARSHIP",
                    "Consolidated portal for state-specific educational scholarships and fee reimbursements for Maharashtra students.",
                    "Domicile of Maharashtra belonging to SC/ST/OBC/VJNT/SBC/EWS categories pursuing higher education.",
                    "Tuition fee concession, exam fee reimbursement, and maintenance allowance directly transferred via DBT.",
                    "https://mahadbt.maharashtra.gov.in", "2026-11-30"));

            governmentPrograms.put(12, new GovernmentProgram(12, "National Apprenticeship Training Scheme (NATS)",
                    "Board of Practical Training / Ministry of Education", "APPRENTICESHIP",
                    "Institutional bridge providing technical graduates and diploma holders practical on-the-job industrial skills.",
                    "Fresh graduates and diploma holders in Engineering/Technology and general streams passing within last 3 years.",
                    "Central government guaranteed monthly stipend directly credited via DBT, recognized certificate of proficiency.",
                    "https://nats.education.gov.in", "Ongoing"));
        }

        private void addStudent(int userId, String name, String email, String phone, String state, String city,
                                int studentId, String dob, String gender, String cat, String branch, String eduLevel,
                                String college, int gradYear, String bio, boolean vis, boolean consent, int completion) {
            users.put(userId, new User(userId, name, email, phone, "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "salt1234", "STUDENT", state, city));
            StudentProfile sp = new StudentProfile();
            sp.setId(studentId); sp.setUserId(userId); sp.setDateOfBirth(dob); sp.setGender(gender);
            sp.setCategory(cat); sp.setBranch(branch); sp.setEducationLevel(eduLevel); sp.setCollege(college);
            sp.setGraduationYear(gradYear); sp.setBio(bio); sp.setProfileVisibility(vis); sp.setConsentGiven(consent);
            sp.setProfileCompletion(completion);
            sp.setName(name); sp.setEmail(email); sp.setPhone(phone); sp.setState(state); sp.setCity(city);
            studentProfiles.put(studentId, sp);
        }

        private void addOpportunity(int id, Integer empId, String title, String desc, String type, String org,
                                    String loc, String state, String city, String branch, String eduReq,
                                    String skills, String cat, String stipend, String deadline, String url, boolean verified) {
            Opportunity op = new Opportunity();
            op.setId(id); op.setEmployerId(empId); op.setTitle(title); op.setDescription(desc);
            op.setType(type); op.setOrganization(org); op.setLocation(loc); op.setState(state); op.setCity(city);
            op.setBranch(branch); op.setEducationRequirement(eduReq); op.setSkills(skills); op.setCategory(cat);
            op.setStipendOrSalary(stipend); op.setDeadline(deadline); op.setSourceUrl(url); op.setVerified(verified);
            op.setCreatedAt("2026-08-01");
            opportunities.put(id, op);
        }

        private void addApplication(int id, int oppId, int studentId, String status, String note, String date) {
            Application a = new Application();
            a.setId(id); a.setOpportunityId(oppId); a.setStudentId(studentId); a.setStatus(status);
            a.setCoverNote(note); a.setAppliedAt(date); a.setUpdatedAt(date);
            applications.put(id, a);
        }
    }
}
