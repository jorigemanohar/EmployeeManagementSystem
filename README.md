# Employee Management & Payroll System

A full-stack web application for managing employees, departments, attendance, leave requests, payroll records, and salary slips. Built with **React, Spring Boot, MySQL, and JWT authentication**.

## Project Overview

The Employee Management & Payroll System provides separate functionality for administrators and employees.

- **Administrators:** Manage employee records, departments, attendance, leave requests, and payroll.
- **Employees:** Access their own profile, attendance records, leave requests, payroll information, and salary-slip PDFs.

## Features

### Authentication and Security
- JWT-based authentication
- Spring Security integration
- Role-based access for ADMIN and EMPLOYEE users
- BCrypt password hashing
- Protected REST API endpoints
- Employee account linking and validation

### Employee Management
- Create, view, update, and delete employee records
- Search employee information
- Associate employees with departments
- Support employee login accounts

### Department Management
- Create, view, update, and delete departments
- Manage department information

### Attendance Management
- Record employee attendance
- View, update, and delete attendance records
- Retrieve attendance by employee
- Prevent duplicate attendance records for the same employee and date

### Leave Management
- Submit and view leave requests
- Manage leave records
- Approve or reject leave requests
- Track leave status
- Restrict employee self-service to the linked employee account

### Payroll Management
- Create, view, update, and delete payroll records
- Manage basic salary, allowances, and deductions
- Calculate net salary through the payroll service
- Validate payroll data
- Prevent duplicate payroll records for the same employee and month

### Salary Slips
- Generate salary slips in PDF format
- Download salary-slip PDFs
- Include employee and salary information
- Restrict employee downloads to their own payroll records

### Dashboard and Employee Self-Service
- Separate administrator and employee functionality
- Employee profile access
- Employee-specific attendance, leave, and payroll information
- Access to available salary slips

## Technology Stack

| Category | Technologies |
|---|---|
| Frontend | React, JavaScript, HTML5, CSS3 |
| HTTP client | Axios |
| Backend | Java, Spring Boot, Spring MVC |
| Security | Spring Security, JWT, BCrypt |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8.0 |
| Backend testing | JUnit 5, Mockito, Spring Boot Test |
| Frontend build | npm, Create React App |
| Version control | Git, GitHub |
| Development environment | IntelliJ IDEA |

## System Architecture

```text
+--------------------------------------+
|            React Frontend            |
|          http://localhost:3000       |
|                                      |
| Login, Dashboard, Employee Features  |
+-------------------+------------------+
                    |
                    | REST APIs
                    | JWT Authentication
                    v
+--------------------------------------+
|        Spring Boot Backend           |
|          http://localhost:8080       |
|                                      |
| Controllers, Services, Security      |
| Payroll and PDF Generation           |
+-------------------+------------------+
                    |
                    | Spring Data JPA
                    | Hibernate
                    v
+--------------------------------------+
|             MySQL 8.0                |
|                                      |
|        employee_management            |
+--------------------------------------+
```

## Prerequisites

Install the following before running the application:

- Java Development Kit compatible with the backend's `pom.xml`
- MySQL 8.0
- Node.js and npm
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/jorigemanohar/EmployeeManagementSystem.git
cd EmployeeManagementSystem
```

### 2. Configure MySQL

Start your MySQL server and create the database:

```sql
CREATE DATABASE IF NOT EXISTS employee_management;
```

The backend datasource configuration is in `backend/src/main/resources/application.properties`.

It expects these environment variables:

- `DB_PASSWORD` — password for the configured MySQL user
- `JWT_SECRET` — secret used by the application's JWT configuration

Set both variables in your local environment before starting the backend. Use your own values and **never commit real passwords or signing secrets to GitHub**.

The current configuration uses the MySQL username `root` and connects to `localhost:3306/employee_management`. Adjust the configuration if your local MySQL setup differs.

### 3. Start the Spring Boot Backend

Open a terminal in the backend directory:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The API is configured to run at:

```text
http://localhost:8080
```

Keep the backend terminal running.

### 4. Start the React Frontend

Open a separate terminal in the frontend directory:

```powershell
cd frontend
npm install
npm start
```

The development server normally opens at:

```text
http://localhost:3000
```

The frontend API service is currently configured to use `http://localhost:8080/api`. Keep the backend running while using features that call the API.

## Running Tests

### Backend Tests

From the `backend` directory, ensure MySQL is running and the required environment variables are configured. Run:

```powershell
.\mvnw.cmd clean test
```

The full backend test suite was verified with **84 tests passing, zero failures, and zero errors** in the local development environment.

### Frontend Production Build

From the `frontend` directory, run:

```powershell
npm run build
```

The React production build has been verified to compile successfully.

### Frontend Tests

From the `frontend` directory, run:

```powershell
npm test -- --watchAll=false
```

## Repository Structure

```text
EmployeeManagementSystem/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw.cmd
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   └── services/
│   ├── package.json
│   └── package-lock.json
└── README.md
```

## Project Status

- Backend modules and controller tests implemented
- Full backend test suite passing in the local development environment
- React production build successful
- Local login and application workflows verified in the browser
- GitHub repository synchronized with the `main` branch

**Note:** The current frontend API URL and database configuration are intended for local development. Before deploying publicly, configure environment-specific API URLs, production secrets, database access, HTTPS, and hosting settings.

## Author

**Manohar Jorige**

GitHub: https://github.com/jorigemanohar
