\# Employee Management \& Payroll System



A full-stack \*\*Employee Management and Payroll System\*\* built using \*\*React.js, Spring Boot, MySQL, and JWT Authentication\*\*.



This project is designed to manage employees, departments, attendance, leave requests, payroll, salary slips, and role-based user access through a secure REST API.



\---



\## 📌 Project Overview



The Employee Management \& Payroll System is a web-based application that provides separate functionality for \*\*ADMIN\*\* and \*\*EMPLOYEE\*\* users.



The system allows administrators to manage employee information, departments, attendance, leave requests, and payroll records, while employees can access their own profile, attendance, leave information, payroll details, and salary slips.



\---



\## ✨ Features



\### 🔐 Authentication \& Security



\- JWT-based authentication

\- Spring Security integration

\- Role-based authorization

\- ADMIN and EMPLOYEE roles

\- BCrypt password hashing

\- Secure employee account creation

\- Protected REST APIs

\- Stateless authentication

\- CORS configuration

\- Authentication validation

\- Unauthorized request protection



\---



\### 👨‍💼 Employee Management



\- Add new employees

\- View all employees

\- View employee details

\- Update employee information

\- Delete employees

\- Search employees

\- Create employee login accounts

\- Assign employee information to departments



\---



\### 🏢 Department Management



\- Create departments

\- View departments

\- Update departments

\- Delete departments

\- Manage department information



\---



\### 🕒 Attendance Management



\- Mark employee attendance

\- View attendance records

\- Update attendance records

\- Delete attendance records

\- Track attendance by employee

\- Prevent duplicate attendance records for the same employee and date



\---



\### 📝 Leave Management



\- Submit leave requests

\- View leave requests

\- Update leave requests

\- Delete leave requests

\- Approve leave requests

\- Reject leave requests

\- Track leave status

\- Employee-specific leave access



\---



\### 💰 Payroll Management



\- Create payroll records

\- View payroll records

\- Update payroll records

\- Delete payroll records

\- Manage salary components

\- Calculate net salary

\- Validate payroll information

\- Prevent duplicate payroll records

\- Prevent invalid negative net salary



\---



\### 📄 Salary Slip



\- Generate employee salary slips

\- Download salary slips as PDF

\- Display salary information

\- Employee-specific salary slip access



\---



\### 📊 Dashboard



\#### ADMIN Dashboard



Displays information related to:



\- Employees

\- Departments

\- Attendance

\- Leave

\- Payroll



\#### EMPLOYEE Dashboard



Provides access to:



\- Employee profile

\- Attendance

\- Leave requests

\- Payroll information

\- Salary slips



\---



\# 🛠️ Technology Stack



\## Frontend



\- React.js

\- JavaScript

\- HTML5

\- CSS3

\- Axios



\## Backend



\- Java

\- Spring Boot

\- Spring Security

\- Spring Data JPA

\- Hibernate

\- REST APIs

\- JWT Authentication

\- BCrypt



\## Database



\- MySQL 8.0



\## Development Tools



\- IntelliJ IDEA

\- MySQL Workbench

\- Postman

\- Git

\- GitHub

\- Maven

\- npm



\---



\# 🏗️ System Architecture



```text

&#x20;                   ┌──────────────────────┐

&#x20;                   │    React Frontend    │

&#x20;                   │      Port: 3000      │

&#x20;                   └──────────┬───────────┘

&#x20;                              │

&#x20;                              │ REST API

&#x20;                              │ JWT Token

&#x20;                              ▼

&#x20;                   ┌──────────────────────┐

&#x20;                   │   Spring Boot API    │

&#x20;                   │      Port: 8080      │

&#x20;                   └──────────┬───────────┘

&#x20;                              │

&#x20;                       JPA / Hibernate

&#x20;                              │

&#x20;                              ▼

&#x20;                   ┌──────────────────────┐

&#x20;                   │    MySQL Database    │

&#x20;                   │ employee\_management  │

&#x20;                   └──────────────────────┘

