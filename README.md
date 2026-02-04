# ⚙️ EduChain - Backend API

**EduChain** is a university-level **Skill Exchange Platform** designed to connect **Learners** with **Mentors**. This repository contains the **RESTful Backend API** built with **Java Spring Boot**, serving as the core engine for user authentication, data management, and real-time interactions.

🚀 **Live API Base URL:** [https://educhain-backend-7cv0.onrender.com](https://educhain-backend-7cv0.onrender.com)
🔗 **Frontend Repository:** [https://github.com/Mangesh-Surwase/educhain-frontend]

---

## 📖 Table of Contents
- [About the Project](#-about-the-project)
- [Tech Stack](#-tech-stack)
- [Key Features](#-key-features)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Endpoints](#-api-endpoints)
- [Folder Structure](#-folder-structure)
- [Contact](#-contact)

---

## 📝 About the Project
The EduChain Backend is designed to handle complex logic for a peer-to-peer learning environment. It manages the lifecycle of users, skills, connection requests, and meeting schedules securely and efficiently.

It follows the **Controller-Service-Repository** architecture pattern to ensure code maintainability and scalability.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Framework** | Java Spring Boot 3.0+ |
| **Language** | Java 17 |
| **Database** | MySQL 8.0 (Hosted on Aiven Cloud) |
| **ORM / Data** | Spring Data JPA, Hibernate |
| **Security** | Spring Security, JWT (JSON Web Tokens), BCrypt |
| **Email Service** | Brevo API (SMTP) for OTP & Notifications |
| **Build Tool** | Maven |
| **Deployment** | Render (Cloud Platform) |

---

## ✨ Key Features

### 🔐 Authentication & Security
- **JWT Authentication:** Stateless security mechanism for protecting API endpoints.
- **Email Verification:** Users must verify their email via OTP (One Time Password) during registration.
- **Password Encryption:** Uses `BCryptPasswordEncoder` for storing passwords securely.

### 👤 User Management
- **Profile Customization:** Users can update bios, details, and Profile Pictures (Stored as Base64 strings).
- **Role-Based Access:** Distinguishes between Learners and Mentors (logic-based).

### 📚 Skill Marketplace
- **CRUD Operations:** Users can Post, Edit, and Delete skills they want to Teach or Learn.
- **Search & Filter:** Advanced querying to find skills based on categories or keywords.

### 🤝 Networking & Scheduling
- **Connection Requests:** Learners can send requests to Mentors.
- **Meeting Scheduler:** Integrated system to schedule and track learning sessions.
- **Notification System:** Real-time updates for requests and meeting status.

---

## 🗄 Database Schema
The application uses a relational database model with the following primary entities:
- **Users:** Stores personal info, credentials, and authentication status.
- **Skills:** Stores skill details, category, and proficiency.
- **SkillRequests:** Manages the status of connection requests (Pending/Accepted).
- **Meetings:** Stores schedule details (Date, Time, Link) for sessions.
- **Notifications:** Logs user activities and alerts.

---

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven installed
- MySQL Database (Local or Cloud)

### Installation

1. **Clone the repository**
   ```bash
   git clone [https://github.com/Mangesh-Surwase/educhain-backend]