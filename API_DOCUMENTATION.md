# Career Guidance Management System (CGMS) - REST API Documentation

Comprehensive API Reference & Postman Collection Guide for CGMS Spring Boot 3 Backend.

- **Base URL**: `http://localhost:8080/api`
- **Authentication**: JWT Bearer Token (`Authorization: Bearer <token>`)

---

## 1. 🔑 Authentication Module (`/api/auth`)

### 1.1 Register User
- **Endpoint**: `POST /api/auth/register`
- **Access**: Public (Students can self-register; Counsellor/Admin registration requires `ROLE_ADMIN`)
- **Request Body**:
  ```json
  {
    "fullName": "Jane Smith",
    "email": "jane.smith@student.com",
    "password": "student123",
    "phone": "+1-800-555-0202",
    "role": "STUDENT",
    "educationLevel": "High School",
    "preferredField": "Biotechnology"
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Registration successful!",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer",
      "id": 5,
      "email": "jane.smith@student.com",
      "fullName": "Jane Smith",
      "role": "STUDENT",
      "message": "Registration successful!"
    }
  }
  ```

### 1.2 Login User
- **Endpoint**: `POST /api/auth/login`
- **Access**: Public
- **Request Body**:
  ```json
  {
    "email": "admin@cgms.com",
    "password": "admin123"
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Login successful!",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer",
      "id": 1,
      "email": "admin@cgms.com",
      "fullName": "System Super Admin",
      "role": "ADMIN",
      "message": "Login successful!"
    }
  }
  ```

---

## 2. 🎓 Student Module (`/api/student`)
**Required Header**: `Authorization: Bearer <STUDENT_JWT_TOKEN>`

### 2.1 List All Counsellors
- **Endpoint**: `GET /api/student/counsellors`
- **Access**: `ROLE_STUDENT`
- **Response** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Counsellors retrieved successfully",
    "data": [
      {
        "id": 2,
        "profileId": 1,
        "name": "Dr. Sarah Connor",
        "email": "sarah.connor@cgms.com",
        "phone": "+1-800-555-0101",
        "qualification": "Ph.D in Computer Science & AI",
        "specialization": "STEM, Software Engineering & AI Careers",
        "experience": 10,
        "bio": "Specialist in guiding students toward software engineering...",
        "profilePhoto": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150"
      }
    ]
  }
  ```

### 2.2 View Counsellor Availability
- **Endpoint**: `GET /api/student/counsellors/{counsellorId}/availability`
- **Access**: `ROLE_STUDENT`
- **Response** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Counsellor available slots retrieved",
    "data": [
      {
        "id": 1,
        "date": "2026-09-27",
        "dayOfWeek": "SUNDAY",
        "startTime": "09:00:00",
        "endTime": "10:00:00",
        "isBooked": false
      }
    ]
  }
  ```

### 2.3 Book Appointment
- **Endpoint**: `POST /api/student/appointments`
- **Access**: `ROLE_STUDENT`
- **Request Body**:
  ```json
  {
    "counsellorId": 2,
    "availabilitySlotId": 1
  }
  ```
- **Response** (`201 Created`):
  ```json
  {
    "success": true,
    "message": "Appointment booked successfully",
    "data": {
      "id": 10,
      "counsellorId": 2,
      "counsellorName": "Dr. Sarah Connor",
      "studentId": 5,
      "studentName": "Jane Smith",
      "availabilitySlotId": 1,
      "slotDate": "2026-09-27",
      "startTime": "09:00:00",
      "endTime": "10:00:00",
      "status": "PENDING"
    }
  }
  ```

### 2.4 View Student Appointment History
- **Endpoint**: `GET /api/student/appointments`
- **Access**: `ROLE_STUDENT`
- **Response** (`200 OK`): Lists student's appointment history with status badges.

### 2.5 Process Payment for Appointment
- **Endpoint**: `POST /api/student/payments`
- **Access**: `ROLE_STUDENT`
- **Request Body**:
  ```json
  {
    "appointmentId": 10,
    "amount": 500.00,
    "paymentMethod": "CREDIT_CARD"
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Payment completed successfully",
    "data": {
      "id": 1,
      "appointmentId": 10,
      "studentId": 5,
      "amount": 500.00,
      "status": "SUCCESS",
      "transactionId": "TXN-8F92A10C",
      "paymentMethod": "CREDIT_CARD"
    }
  }
  ```

### 2.6 View Assigned Tasks
- **Endpoint**: `GET /api/student/tasks`
- **Access**: `ROLE_STUDENT`

### 2.7 Submit Task Assignment
- **Endpoint**: `POST /api/student/tasks/{taskId}/submit`
- **Access**: `ROLE_STUDENT`
- **Request Body**:
  ```json
  {
    "submissionText": "Here is my completed skill assessment report on AI careers.",
    "submissionFile": "https://drive.google.com/sample-file.pdf"
  }
  ```

### 2.8 View Evaluated Results
- **Endpoint**: `GET /api/student/results`
- **Access**: `ROLE_STUDENT`

---

## 3. 👨‍🏫 Counsellor Module (`/api/counsellor`)
**Required Header**: `Authorization: Bearer <COUNSELLOR_JWT_TOKEN>`

### 3.1 Get/Update Own Profile
- **Endpoints**: `GET /api/counsellor/profile`, `PUT /api/counsellor/profile`
- **Access**: `ROLE_COUNSELLOR`
- **Request Body (PUT)**:
  ```json
  {
    "name": "Dr. Sarah Connor",
    "phone": "+1-800-555-9999",
    "qualification": "Ph.D in AI & Career Psychology",
    "specialization": "Artificial Intelligence & Robotics",
    "experience": 11,
    "bio": "Updated professional bio..."
  }
  ```

### 3.2 Add Availability Slot
- **Endpoint**: `POST /api/counsellor/availability`
- **Access**: `ROLE_COUNSELLOR`
- **Request Body**:
  ```json
  {
    "date": "2026-09-30",
    "startTime": "14:00:00",
    "endTime": "15:00:00"
  }
  ```

### 3.3 Delete Unbooked Slot
- **Endpoint**: `DELETE /api/counsellor/availability/{slotId}`
- **Access**: `ROLE_COUNSELLOR`

### 3.4 View Booked Appointments & Update Status
- **Endpoints**: `GET /api/counsellor/appointments`, `PUT /api/counsellor/appointments/{id}/status`
- **Request Body (PUT)**:
  ```json
  {
    "status": "COMPLETED"
  }
  ```

### 3.5 Assign Task to Student
- **Endpoint**: `POST /api/counsellor/tasks`
- **Access**: `ROLE_COUNSELLOR`
- **Request Body**:
  ```json
  {
    "studentId": 5,
    "title": "Evaluate Python & Data Structures Skillset",
    "description": "Complete the online quiz and submit your score transcript.",
    "dueDate": "2026-10-05"
  }
  ```

### 3.6 Evaluate Student Submission
- **Endpoint**: `PUT /api/counsellor/submissions/{submissionId}/evaluate`
- **Access**: `ROLE_COUNSELLOR`
- **Request Body**:
  ```json
  {
    "marksOrGrade": "A+ (95/100)",
    "remarks": "Excellent analytical skills! Recommended for Data Science track."
  }
  ```

---

## 4. 🛡️ Admin Module (`/api/admin`)
**Required Header**: `Authorization: Bearer <ADMIN_JWT_TOKEN>`

### 4.1 Manage Students Directory & Status
- **Endpoints**: `GET /api/admin/students`, `PUT /api/admin/students/{id}/status`
- **Request Body (PUT)**:
  ```json
  {
    "isActive": false
  }
  ```

### 4.2 Create Counsellor Account
- **Endpoint**: `POST /api/admin/counsellors`
- **Access**: `ROLE_ADMIN`
- **Request Body**:
  ```json
  {
    "name": "Dr. Grace Hopper",
    "email": "grace.hopper@cgms.com",
    "password": "counsellor123",
    "phone": "+1-800-555-0888",
    "qualification": "Ph.D Computer Engineering",
    "specialization": "Compiler Design & Systems",
    "experience": 15,
    "bio": "Pioneer in computing careers and system software guidance."
  }
  ```

### 4.3 View Payment Transactions Log
- **Endpoint**: `GET /api/admin/payments?status=SUCCESS&startDate=2026-09-01&endDate=2026-09-30`
- **Access**: `ROLE_ADMIN`

### 4.4 Dashboard Summary Metrics Report
- **Endpoint**: `GET /api/admin/reports/summary`
- **Access**: `ROLE_ADMIN`
- **Response** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Summary report metrics retrieved",
    "data": {
      "totalStudents": 3,
      "totalCounsellors": 3,
      "totalAppointments": 1,
      "totalRevenue": 500.00,
      "appointmentsByStatus": {
        "PENDING": 0,
        "CONFIRMED": 1,
        "COMPLETED": 0,
        "CANCELLED": 0
      }
    }
  }
  ```

### 4.5 Export CSV Appointments Report
- **Endpoint**: `GET /api/admin/reports/export`
- **Access**: `ROLE_ADMIN`
- **Response Content-Type**: `text/csv`
- **Header**: `Content-Disposition: attachment; filename=cgms_appointments_report.csv`

---

## 🧪 Demo Login Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **ADMIN** | `admin@cgms.com` | `admin123` |
| **COUNSELLOR** | `sarah.connor@cgms.com` | `counsellor123` |
| **COUNSELLOR** | `alan.grant@cgms.com` | `counsellor123` |
| **COUNSELLOR** | `ellie.sattler@cgms.com` | `counsellor123` |
| **STUDENT** | `john.doe@student.com` | `student123` |
| **STUDENT** | `jane.smith@student.com` | `student123` |
| **STUDENT** | `alex.rivera@student.com` | `student123` |
