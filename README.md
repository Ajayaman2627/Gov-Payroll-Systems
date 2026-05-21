# Government Payroll System

A desktop-based payroll management system built with **Java Swing** and **MySQL**, designed to handle employee records, salary structures, allowances, and deductions for a government organisation.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Features](#features)
- [Database Setup](#database-setup)
- [How to Run](#how-to-run)
- [Architecture](#architecture)
- [Modules Breakdown](#modules-breakdown)
- [Known Limitations / In Progress](#known-limitations--in-progress)
- [Security Note](#security-note)
- [Author](#author)

---

## Overview

This system allows payroll administrators to:

- Log in with role-based access control
- Manage government departments and employee records
- Configure grade/step pay structures
- Define and assign allowance types (flat amount or % of salary) and deduction types
- Run payroll calculations per employee

The application uses a layered architecture — **UI → DAO → MySQL** — keeping the business logic, data access, and presentation cleanly separated.

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 17+                            |
| GUI          | Java Swing                          |
| Database     | MySQL 8.x                           |
| JDBC Driver  | mysql-connector-j-9.6.0             |
| IDE          | VS Code with Java Extension Pack    |
| Build        | Manual `javac` / VS Code Java build |

---

## Project Structure

```
GovernmentPayrollSystem/
├── src/
│   ├── App.java                        # Placeholder entry point
│   ├── app/
│   │   ├── Main.java                   # DB connection test runner
│   │   └── Session.java                # Logged-in user state (userId, username, role)
│   ├── db/
│   │   └── DBConnection.java           # MySQL JDBC connection factory
│   ├── model/                          # Plain Java objects (POJOs)
│   │   ├── Employee.java
│   │   ├── Department.java
│   │   ├── Grade.java
│   │   ├── AllowanceType.java
│   │   ├── AllowanceCalcMode.java
│   │   ├── DeductionType.java
│   │   ├── EmployeeAllowance.java
│   │   ├── EmployeeDeduction.java
│   │   └── EmployeeMini.java
│   ├── dao/                            # Data Access Objects (SQL queries)
│   │   ├── DB.java
│   │   ├── UserDAO.java
│   │   ├── EmployeeDAO.java
│   │   ├── EmployeeMiniDAO.java
│   │   ├── DepartmentDAO.java
│   │   ├── GradeDAO.java
│   │   ├── AllowanceTypeDAO.java
│   │   ├── EmployeeAllowanceDAO.java
│   │   ├── DeductionTypeDAO.java
│   │   └── EmployeeDeductionDAO.java
│   └── ui/                             # Swing GUI screens
│       ├── LoginFrame.java
│       ├── DashboardFrame.java
│       ├── EmployeesFrame.java
│       ├── DepartmentsFrame.java
│       ├── AllowancesMenuFrame.java
│       ├── AllowancesFrame.java
│       ├── AllowanceTypesFrame.java
│       ├── AllowanceTypeFormDialog.java
│       ├── EmployeeAllowancesFrame.java
│       ├── EmployeeAllowanceFormDialog.java
│       ├── DeductionsFrame.java
│       ├── ManageDeductionTypePanel.java
│       ├── ManageEmployeeDeductionPanel.java
│       ├── PayStructureFrame.java
│       └── PayrollRunFrame.java
├── lib/
│   └── mysql-connector-j-9.6.0.jar    # MySQL JDBC dependency
├── bin/                                # Compiled .class files (git-ignored)
├── .gitignore
└── README.md
```

---

## Features

### Completed
- **Login** — Username + password authentication against the `user_account` table; role is loaded on login (e.g., ADMIN, PAYROLL_OFFICER)
- **Session management** — Logged-in user stored in a static `Session` class, used across all screens
- **Dashboard** — Navigation hub with tile buttons for each module
- **Departments** — Full CRUD (create, read, update, delete) with department code and active/inactive toggle
- **Employees** — Full CRUD including: first/last name, email, phone, DOB, hire date, basic salary, department assignment, grade/step assignment, active status
- **Allowance Types** — Define allowance categories with:
  - Calculation mode: flat `AMOUNT` or `PERCENT` of basic salary
  - Default value
  - Taxable flag
- **Employee Allowances** — Assign specific allowance types to individual employees with custom overrides
- **Deduction Types** — Manage deduction categories (tax, pension, etc.)
- **Employee Deductions** — Assign deductions to employees

### In Progress / Placeholders
- **Pay Structure** — Grade/step salary matrix (`PayStructureFrame` — UI placeholder)
- **Payroll Run** — Automated payroll calculation and payslip generation (`PayrollRunFrame` — UI placeholder)

---

## Database Setup

### 1. Create the MySQL database and user

```sql
CREATE DATABASE ge_payroll;
CREATE USER 'payroll_app'@'localhost' IDENTIFIED BY 'payroll123';
GRANT ALL PRIVILEGES ON ge_payroll.* TO 'payroll_app'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Create the core tables

```sql
USE ge_payroll;

-- Roles
CREATE TABLE role (
    role_id   INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL
);

-- User accounts
CREATE TABLE user_account (
    user_id       INT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       INT NOT NULL,
    is_active     TINYINT(1) DEFAULT 1,
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);

-- Departments
CREATE TABLE department (
    dept_id    INT PRIMARY KEY AUTO_INCREMENT,
    dept_name  VARCHAR(100) NOT NULL,
    dept_code  VARCHAR(20)  NOT NULL UNIQUE,
    is_active  TINYINT(1) DEFAULT 1
);

-- Grades
CREATE TABLE grade (
    grade_id   INT PRIMARY KEY AUTO_INCREMENT,
    grade_name VARCHAR(50) NOT NULL
);

-- Employees
CREATE TABLE employee (
    emp_id        INT PRIMARY KEY AUTO_INCREMENT,
    first_name    VARCHAR(50)    NOT NULL,
    last_name     VARCHAR(50)    NOT NULL,
    email         VARCHAR(100),
    phone         VARCHAR(20),
    dob           DATE,
    hire_date     DATE,
    basic_salary  DECIMAL(12,2)  NOT NULL DEFAULT 0.00,
    dept_id       INT NOT NULL,
    grade_id      INT,
    step_no       INT DEFAULT 1,
    is_active     TINYINT(1) DEFAULT 1,
    FOREIGN KEY (dept_id)  REFERENCES department(dept_id),
    FOREIGN KEY (grade_id) REFERENCES grade(grade_id)
);

-- Allowance types
CREATE TABLE allowance_type (
    allow_type_id INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    calc_type     ENUM('AMOUNT','PERCENT') DEFAULT 'AMOUNT',
    default_value DECIMAL(12,2) DEFAULT 0.00,
    is_taxable    TINYINT(1) DEFAULT 0
);

-- Employee allowances
CREATE TABLE employee_allowance (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    emp_id        INT NOT NULL,
    allow_type_id INT NOT NULL,
    amount        DECIMAL(12,2),
    FOREIGN KEY (emp_id)        REFERENCES employee(emp_id),
    FOREIGN KEY (allow_type_id) REFERENCES allowance_type(allow_type_id)
);

-- Deduction types
CREATE TABLE deduction_type (
    ded_type_id   INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    calc_type     ENUM('AMOUNT','PERCENT') DEFAULT 'AMOUNT',
    default_value DECIMAL(12,2) DEFAULT 0.00,
    is_pretax     TINYINT(1) DEFAULT 0
);

-- Employee deductions
CREATE TABLE employee_deduction (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    emp_id      INT NOT NULL,
    ded_type_id INT NOT NULL,
    amount      DECIMAL(12,2),
    FOREIGN KEY (emp_id)      REFERENCES employee(emp_id),
    FOREIGN KEY (ded_type_id) REFERENCES deduction_type(ded_type_id)
);
```

### 3. Create additional tables (Pay Structure & Payroll)

```sql
-- Pay Structure: grade + step → salary amount
CREATE TABLE pay_structure (
    pay_structure_id INT PRIMARY KEY AUTO_INCREMENT,
    grade_id         INT NOT NULL,
    step_no          INT NOT NULL,
    salary_amount    DECIMAL(12,2) NOT NULL,
    UNIQUE KEY uq_grade_step (grade_id, step_no),
    FOREIGN KEY (grade_id) REFERENCES grade(grade_id)
);

-- Payroll run header (one row per payroll run)
CREATE TABLE payroll_run (
    run_id             INT PRIMARY KEY AUTO_INCREMENT,
    run_date           DATE        NOT NULL,
    pay_period_month   INT         NOT NULL,
    pay_period_year    INT         NOT NULL,
    run_by_user_id     INT,
    status             VARCHAR(20) DEFAULT 'COMPLETED',
    FOREIGN KEY (run_by_user_id) REFERENCES user_account(user_id)
);

-- Payroll record: one row per employee per run
CREATE TABLE payroll_record (
    record_id         INT PRIMARY KEY AUTO_INCREMENT,
    run_id            INT           NOT NULL,
    emp_id            INT           NOT NULL,
    basic_salary      DECIMAL(12,2),
    total_allowances  DECIMAL(12,2),
    gross_pay         DECIMAL(12,2),
    total_deductions  DECIMAL(12,2),
    net_pay           DECIMAL(12,2),
    FOREIGN KEY (run_id) REFERENCES payroll_run(run_id),
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id)
);
```

### 4. Insert seed data

```sql
-- Roles
INSERT INTO role (role_name) VALUES ('ADMIN'), ('PAYROLL_OFFICER');

-- Default admin user (password stored as plain text for dev — hash in production)
INSERT INTO user_account (username, password_hash, role_id) VALUES ('admin', 'admin123', 1);

-- Sample department
INSERT INTO department (dept_name, dept_code) VALUES ('Finance', 'FIN'), ('HR', 'HRD');

-- Sample grade
INSERT INTO grade (grade_name) VALUES ('Grade 1'), ('Grade 2'), ('Grade 3');
```

---

## How to Run

### Prerequisites

| Tool         | Version   |
|--------------|-----------|
| Java JDK     | 17 or higher |
| MySQL Server | 8.x       |
| VS Code      | Any recent version |
| Java Extension Pack for VS Code | Latest |

### Steps

1. **Clone the repo**
   ```bash
   git clone https://github.com/<your-username>/GovernmentPayrollSystem.git
   cd GovernmentPayrollSystem
   ```

2. **Set up the database** — follow the [Database Setup](#database-setup) section above

3. **Update DB credentials** (if you changed them)
   Open `src/db/DBConnection.java` and update:
   ```java
   private static final String URL  = "jdbc:mysql://localhost:3306/ge_payroll?...";
   private static final String USER = "payroll_app";
   private static final String PASS = "payroll123";
   ```

4. **Open in VS Code**
   - Open the `GovernmentPayrollSystem` folder
   - VS Code will automatically detect the Java project
   - Wait for the Java Language Server to index the project

5. **Run the app**
   - Open `src/ui/LoginFrame.java`
   - Click **Run** (▷) above the `main` method
   - Or use the VS Code Run Configuration

6. **Login**
   - Username: `admin`
   - Password: `admin123`

---

## Architecture

```
┌──────────────────────────────────────────────┐
│                  UI Layer (Swing)             │
│  LoginFrame → DashboardFrame → Module Frames │
└────────────────────┬─────────────────────────┘
                     │ calls
┌────────────────────▼─────────────────────────┐
│              DAO Layer (JDBC)                 │
│  UserDAO, EmployeeDAO, DepartmentDAO, ...     │
└────────────────────┬─────────────────────────┘
                     │ SQL via PreparedStatements
┌────────────────────▼─────────────────────────┐
│            MySQL Database (ge_payroll)        │
│  user_account, employee, department, ...      │
└──────────────────────────────────────────────┘
```

**Session state** is held in the static `app.Session` class and populated at login, making the logged-in user's ID, username, and role available application-wide.

---

## Modules Breakdown

| Module | Entry Point | DAO | Status |
|---|---|---|---|
| Login | `LoginFrame` | `UserDAO` | Complete |
| Dashboard | `DashboardFrame` | — | Complete |
| Departments | `DepartmentsFrame` | `DepartmentDAO` | Complete |
| Employees | `EmployeesFrame` | `EmployeeDAO`, `GradeDAO` | Complete |
| Allowance Types | `AllowanceTypesFrame` | `AllowanceTypeDAO` | Complete |
| Employee Allowances | `EmployeeAllowancesFrame` | `EmployeeAllowanceDAO` | Complete |
| Deduction Types | `ManageDeductionTypePanel` | `DeductionTypeDAO` | Complete |
| Employee Deductions | `ManageEmployeeDeductionPanel` | `EmployeeDeductionDAO` | Complete |
| Pay Structure | `PayStructureFrame` | `PayStructureDAO` | Complete |
| Payroll Run + Payslips | `PayrollRunFrame`, `PayslipDialog` | `PayrollDAO` | Complete |

---

## Known Limitations

- **Password hashing** — Passwords are currently stored and compared as plain text in the dev build; production should use bcrypt or SHA-256
- **No input validation layer** — Basic null/empty checks exist in the UI but no centralised validator
- **Single-user sessions** — No concurrent user support; the `Session` class is a static singleton

---

## Security Note

The database credentials in `src/db/DBConnection.java` are hardcoded for development convenience:

```java
private static final String USER = "payroll_app";
private static final String PASS = "payroll123";
```

**Before deploying to any shared or production environment**, externalise these using environment variables or a `.properties` config file (excluded from version control via `.gitignore`).

---

## Author

**Ajay Amank**  
Java Developer  

Built as a portfolio project demonstrating:
- Java Swing desktop application development
- JDBC / MySQL integration using the DAO pattern
- Role-based authentication
- Multi-screen navigation and session management
- Clean separation of model, data access, and UI layers
