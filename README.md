# 🏥 Praxis Booking Appointment – REST API (Backend)
Spring Boot backend powering the Praxis Booking Appointment System.

## 📌 Overview
Implements:
- JWT authentication
- Role-based authorization
- Booking rules (20-minute intervals)
- Doctor working hours
- Assistant-controlled appointments
- Admin user/role management
- Notification scheduler
- Global exception JSON handling

---

## 🧱 Architecture

React SPA → REST API → Service Layer → JPA → DB (Docker)
↓
JWT Security
↓
Notifications Scheduler

---

## 🔐 Roles

| Role | Description |
|------|-------------|
| ADMIN | Full access |
| DOCTOR | Manage own schedule & appointments |
| ASSISTANT | Manage appointments for assigned doctors |
| USER | Book/manage personal appointments |

Protected endpoints:
/api/admin/** → ADMIN
/api/doctor/** → DOCTOR
/api/assistant/** → ASSISTANT
/api/appointments/** → USER
/api/auth/** → PUBLIC

---

## 🔥 Global Exception Handling

Backend uses @RestControllerAdvice with unified JSON format for errors:

```json
{
  "timestamp": "2025-02-10T10:20:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Error message",
  "path": "/api/example"
}

---

## 🚀 Run Backend

1. Start DB (Docker)
docker run --name praxis-db -e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_DATABASE=praxis -p 3306:3306 -d mysql:8

2. Configure Spring
spring.datasource.url=jdbc:mysql://localhost:3306/praxis
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

3. Start Application
mvn spring-boot:run

---

## 🧪 Main Endpoints Summary

Authentication

POST /api/auth/register
POST /api/auth/login

Users

GET /api/users/me
PUT /api/users/me
PUT /api/users/me/change-password

Doctors

GET /api/doctors
GET /api/doctors/{id}

Appointments

POST /api/appointments/book/{doctorId}
GET /api/appointments/my
PUT /api/appointments/{id}
DELETE /api/appointments/{id}

Doctor Panel

GET /api/doctor/appointments
PUT /api/doctor/appointments/{id}/move
PUT /api/doctor/working-hours

Assistant Panel

GET /api/assistant/doctors
GET /api/assistant/doctor/{id}/appointments
POST /api/assistant/doctor/{id}/appointments
DELETE /api/assistant/appointments/{id}

Admin Panel

GET /api/admin/users
PUT /api/admin/users/{id}
POST /api/admin/assistant/{assistantId}/assign/{doctorId}
DELETE /api/admin/assistant/{assistantId}/unassign/{doctorId}

Notifications

GET /api/notifications/my
POST /api/notifications/{id}/read

---

## 🏁 Conclusion

The backend provides a complete and secure REST API powering the Praxis Booking Appointment System.

