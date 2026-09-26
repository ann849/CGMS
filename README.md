# Career Guidance Management System (CGMS)

A full-stack enterprise scaffold for a **Career Guidance Management System**, built with Java Spring Boot 3.x, MySQL, and a responsive Bootstrap 5 web frontend.

---

## 🏗️ Architecture & Project Structure

```text
CGMS/
├── backend/                             # Spring Boot 3.x Backend Service
│   ├── pom.xml                          # Maven dependencies & configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/cgms/
│       │   │   ├── CgmsApplication.java  # Main Spring Boot Entrypoint
│       │   │   ├── config/              # Security & CORS configuration
│       │   │   │   ├── CorsConfig.java
│       │   │   │   └── SecurityConfig.java
│       │   │   ├── controller/          # REST API Controllers
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── StudentController.java
│       │   │   │   ├── CounsellorController.java
│       │   │   │   └── AdminController.java
│       │   │   ├── dto/                 # Data Transfer Objects
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── AuthResponse.java
│       │   │   │   ├── UserDto.java
│       │   │   │   └── ApiResponse.java
│       │   │   ├── exception/           # Exception Handling & Custom Exceptions
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── BadRequestException.java
│       │   │   ├── model/               # JPA Entities & Enums
│       │   │   │   ├── Role.java        # Enum (STUDENT, COUNSELLOR, ADMIN)
│       │   │   │   ├── User.java        # Central users table entity
│       │   │   │   ├── StudentDetail.java
│       │   │   │   ├── CounsellorDetail.java
│       │   │   │   └── AdminDetail.java
│       │   │   ├── repository/          # Spring Data JPA Repositories
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── StudentDetailRepository.java
│       │   │   │   ├── CounsellorDetailRepository.java
│       │   │   │   └── AdminDetailRepository.java
│       │   │   ├── security/            # JWT & UserDetailsService
│       │   │   │   ├── JwtTokenProvider.java
│       │   │   │   ├── JwtAuthenticationFilter.java
│       │   │   │   └── CustomUserDetailsService.java
│       │   │   └── service/             # Business Logic Layer
│       │   │       ├── AuthService.java
│       │   │       ├── UserService.java
│       │   │       ├── StudentService.java
│       │   │       ├── CounsellorService.java
│       │   │       └── AdminService.java
│       │   └── resources/
│       │       └── application.properties # MySQL database & JWT config
│       └── test/                        # Unit & Integration Tests
│
└── frontend/                            # Frontend Web Application
    ├── index.html                       # Shared Login & Registration Portal
    ├── css/
    │   └── style.css                    # Custom Glassmorphism & UI theme
    ├── js/
    │   ├── config.js                    # API Base URL & fetch helper
    │   ├── auth.js                      # JWT Token & Session management
    │   └── main.js                      # Shared auth page UI logic
    ├── student/
    │   ├── dashboard.html               # Student Portal View
    │   └── student.js                   # Student dashboard controller
    ├── counsellor/
    │   ├── dashboard.html               # Counsellor Portal View
    │   └── counsellor.js                # Counsellor dashboard controller
    ├── admin/
    │   ├── dashboard.html               # Admin Control Center View
    │   └── admin.js                     # Admin user management logic
    └── assets/                          # Static assets & images placeholder
```

---

## 🗄️ Database Architecture

The authentication model relies on a single **`users`** table for cross-role login, with role-specific detail tables mapped via `@OneToOne` foreign keys:

1. **`users`**: `id`, `email` (unique), `password` (BCrypt encoded), `full_name`, `phone`, `role` (`STUDENT`, `COUNSELLOR`, `ADMIN`), `created_at`, `updated_at`.
2. **`student_details`**: `id`, `user_id` (FK), `education_level`, `institution`, `preferred_field`, `skills`, `career_goal`, `bio`.
3. **`counsellor_details`**: `id`, `user_id` (FK), `qualification`, `specialization`, `experience_years`, `consultation_fee`, `is_available`, `bio`.
4. **`admin_details`**: `id`, `user_id` (FK), `department`, `admin_code`, `access_level`.

---

## 🚀 How to Run the Project

### Prerequisites
- **Java JDK 17** or higher
- **Maven**
- **MySQL Server** (running on port 3306)

---

### Step 1: Database Setup
1. Ensure MySQL is running locally.
2. Create the target database (or let Hibernate auto-create it):
   ```sql
   CREATE DATABASE IF NOT EXISTS career_guidance_db;
   ```
3. Update database credentials in `backend/src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

---

### Step 2: Running the Spring Boot Backend
Navigate to the `backend/` directory and run:

```bash
cd backend
mvn clean spring-boot:run
```

The Spring Boot backend will start on **http://localhost:8080**. Hibernate will automatically create/update the database tables in MySQL.

---

### Step 3: Running the Frontend
1. Open the `frontend/` directory.
2. Launch `index.html` using:
   - **VS Code Live Server** (Extension) at `http://127.0.0.1:5500/frontend/index.html` or `http://localhost:5500/frontend/index.html`
   - Or any simple HTTP server, e.g., using Python:
     ```bash
     cd frontend
     python -m http.server 5500
     ```
3. Open `http://localhost:5500` in your browser.

---

## 📡 API Endpoints Overview

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new user (Student, Counsellor, or Admin) | Public |
| `POST` | `/api/auth/login` | Authenticate user & receive JWT token | Public |
| `GET` | `/api/student/profile` | Get logged-in student details | Student / Admin |
| `PUT` | `/api/student/profile/{userId}` | Update student details | Student / Admin |
| `GET` | `/api/counsellor/all` | List all counsellors | Public / Auth |
| `GET` | `/api/counsellor/profile` | Get counsellor profile | Counsellor / Admin |
| `PUT` | `/api/counsellor/profile/{userId}` | Update counsellor profile | Counsellor / Admin |
| `GET` | `/api/admin/users` | List all registered system users | Admin |
| `DELETE` | `/api/admin/users/{id}` | Delete a user from system | Admin |