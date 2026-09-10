# SakshamSetu

> **A digital platform connecting students, employers, and government opportunities through skill-based matching, eligibility assessment, and accessible career services.**

---

## 📌 About the Project

**SakshamSetu** is a full-stack web application designed to bridge the gap between students, employers, and government skill-development opportunities.

The platform provides students with access to opportunities, government schemes, profiles, applications, certifications, and eligibility information. Employers can discover suitable candidates and manage opportunities, while administrators can manage the platform.

The project combines a web-based frontend with a Java backend, SQL database, and C/C++ components for utility and matching functionality.

---

## 🎯 Objectives

* Connect students with relevant career and skill opportunities.
* Help users discover government programs and schemes.
* Provide eligibility-based opportunity recommendations.
* Support employer-side candidate discovery.
* Manage applications, profiles, notifications, and certificates.
* Provide accessibility-focused features for users.
* Implement skill-based matching functionality.
* Provide a structured and scalable full-stack architecture.

---

## ✨ Key Features

### 👨‍🎓 Student Portal

* Student registration and login
* Student profile management
* Education and skills management
* Opportunity discovery
* Government scheme discovery
* Eligibility assessment
* Application management
* Certificate management
* Notifications
* Accessible student portal

### 🏢 Employer Portal

* Employer registration/login
* Employer profile
* Post opportunities
* View applications
* Search and discover candidates
* Manage recruitment opportunities

### 🏛️ Government Programs

* Government scheme listing
* Program information
* Eligibility-related functionality
* Student-oriented scheme discovery

### 🤖 Skill-Based Matching

SakshamSetu includes a matching component designed to help connect candidates with suitable opportunities based on relevant skills and profile information.

The project also contains a C++ matching algorithm implementation.

### ♿ Accessibility

The frontend includes accessibility-oriented functionality and a dedicated accessible student portal.

---

## 🏗️ Project Structure

```text
SakshamSetu/
│
├── frontend/
│   ├── css/
│   ├── js/
│   ├── images/
│   ├── uploads/
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── student-dashboard.html
│   ├── employer-dashboard.html
│   └── ...
│
├── backend/
│   ├── src/
│   │   └── com/govskill/
│   │       ├── controllers/
│   │       ├── dao/
│   │       ├── models/
│   │       ├── services/
│   │       └── utils/
│   └── webapp/
│       └── WEB-INF/
│
├── database/
│   ├── database.sql
│   └── seed.sql
│
├── cpp/
│   └── matching_algorithm.cpp
│
├── c/
│   └── utility.c
│
├── compile.bat
├── run.bat
├── TEE_IMPLEMENTATION.md
└── README.md
```

---

## 🛠️ Technology Stack

### Frontend

* HTML5
* CSS3
* JavaScript
* Responsive Web Design
* Accessibility-focused UI

### Backend

* Java
* Servlet-based architecture
* DAO pattern
* Service layer
* REST-style API functionality

### Database

* SQL
* Relational database architecture
* `database.sql` for database structure
* `seed.sql` for initial/sample data

### Additional Components

* C
* C++
* Trusted Execution Environment (TEE) related implementation
* C/C++ matching and utility components

---

## 🗄️ Database

The database scripts are available in:

```text
database/database.sql
database/seed.sql
```

### Database Setup

1. Create the required database using your SQL database system.
2. Execute:

```text
database/database.sql
```

3. Load initial/sample data using:

```text
database/seed.sql
```

4. Configure the backend database connection according to your local environment.

> **Note:** GitHub stores the SQL files but does not provide a live MySQL/database server for the application.

---

## 🚀 Running the Project Locally

### 1. Clone the Repository

```bash
git clone https://github.com/vishalnarkhede463-debug/SakshamSetu.git
```

```bash
cd SakshamSetu
```

### 2. Configure the Database

Import the SQL files from:

```text
database/
```

Then configure the database connection used by the backend.

### 3. Compile the Project

On Windows, the project includes:

```text
compile.bat
```

Run it from the project directory.

### 4. Start the Application

The project also includes:

```text
run.bat
```

Run the script according to your local Java/server configuration.

### 5. Open the Frontend

The main frontend entry point is:

```text
frontend/index.html
```

For development, you can use VS Code with a local development server such as Live Server.

---

## 🔐 Security

Do **not** commit sensitive information such as:

* Database passwords
* API keys
* Authentication secrets
* Private credentials
* `.env` files containing secrets

Use environment variables or local configuration files for sensitive credentials.

---

## 📁 Important Files

| File                         | Purpose                          |
| ---------------------------- | -------------------------------- |
| `frontend/index.html`        | Main application frontend        |
| `frontend/login.html`        | User login                       |
| `frontend/register.html`     | User registration                |
| `database/database.sql`      | Database structure               |
| `database/seed.sql`          | Initial/sample database data     |
| `cpp/matching_algorithm.cpp` | C++ matching functionality       |
| `c/utility.c`                | C utility component              |
| `compile.bat`                | Project compilation script       |
| `run.bat`                    | Project run script               |
| `TEE_IMPLEMENTATION.md`      | TEE implementation documentation |

---

## 🌐 Deployment

The frontend can be deployed using a static web-hosting service.

However, the complete SakshamSetu application contains:

```text
Frontend + Java Backend + Database
```

Therefore, a complete production deployment requires hosting for both the backend and database in addition to the frontend.

---

## 🔮 Future Enhancements

* AI-powered career recommendations
* Advanced resume analysis
* Improved skill matching
* Real-time notifications
* Mobile application
* Advanced employer analytics
* Government API integration
* Cloud database integration
* Enhanced authentication and authorization
* Production-grade deployment and monitoring

---

## 👨‍💻 Developer

**Vishal Narkhede**

Project: **SakshamSetu**

---

## 📜 License

This project is currently intended for educational and development purposes.

A formal open-source license can be added when the project is ready for public distribution.

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.

**SakshamSetu — Connecting Skills, Opportunities & Aspirations.**
