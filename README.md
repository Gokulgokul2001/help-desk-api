# Help Desk API

[![Maven Build](https://github.com/Gokulgokul2001/help-desk-api/actions/workflows/maven.yml/badge.svg)](https://github.com/Gokulgokul2001/help-desk-api/actions/workflows/maven.yml)

Spring Boot REST API for IT Help Desk and Ticket Management.

A secure backend REST API for managing IT support tickets, users, categories, comments, and file attachments.

The application provides JWT-based authentication, role-based authorization, ticket assignment and status management, filtering, pagination, keyword search, statistics, validation, file management, and standardized exception handling.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Database Configuration](#database-configuration)
- [Application Configuration](#application-configuration)
- [Authentication](#authentication)
- [User Roles and Permissions](#user-roles-and-permissions)
- [API Endpoints](#api-endpoints)
  - [Authentication Endpoints](#authentication-endpoints)
  - [Category Endpoints](#category-endpoints)
  - [Ticket Endpoints](#ticket-endpoints)
  - [Comment Endpoints](#comment-endpoints)
  - [Attachment Endpoints](#attachment-endpoints)
  - [Pagination Filtering and Search](#pagination-filtering-and-search)
  - [Ticket Statistics](#ticket-statistics)
- [Ticket Status](#ticket-status)
- [Ticket Priority](#ticket-priority)
- [Validation](#validation)
- [Exception Handling](#exception-handling)
- [Security](#security)
- [File Upload](#file-upload)
- [Postman Testing](#postman-testing)
- [Running the Application](#running-the-application)
- [Maven Commands](#maven-commands)
- [Testing](#testing)
- [GitHub Actions CI](#github-actions-ci)
- [Environment Variables](#environment-variables)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

# Project Overview

The Help Desk API is a backend REST API designed to manage internal IT support requests.

The system allows employees to create support tickets and track their issues. IT Support users can manage tickets assigned to them, while administrators have broader access to manage the help desk system.

The application follows a layered Spring Boot architecture and uses JWT-based authentication with role-based authorization.

### Main capabilities

- User registration and login
- JWT authentication
- Role-based authorization
- IT ticket management
- Ticket assignment
- Ticket status management
- Ticket categories
- Ticket comments
- File attachments
- Ticket filtering
- Pagination
- Sorting
- Keyword search
- Ticket statistics
- Request validation
- Standardized error responses
- Secure file storage
- Automated Maven build and test using GitHub Actions

---

# Features

## Authentication

- User registration
- User login
- JWT token generation
- JWT token validation
- Stateless authentication
- Protected API endpoints
- Authentication error handling

## Authorization

Three application roles are supported:

- `ADMIN`
- `IT_SUPPORT`
- `EMPLOYEE`

Role-based access control is implemented using Spring Security.

## Ticket Management

- Create tickets
- View tickets
- View individual tickets
- Update tickets
- Assign tickets
- Update ticket status
- Delete tickets
- Ticket ownership validation
- Ticket assignment validation
- Ticket priority management
- Ticket category management

## Ticket Search and Filtering

The API supports:

- Keyword search
- Status filtering
- Priority filtering
- Category filtering
- Created-by filtering
- Assigned-to filtering
- Pagination
- Sorting
- Case-insensitive keyword search
- Multiple filter combinations

## Ticket Statistics

The statistics endpoint provides ticket counts based on ticket status and priority.

Supported statistics include:

- Total tickets
- Open tickets
- In-progress tickets
- Closed tickets
- Low priority
- Medium priority
- High priority

Statistics are scoped according to the authenticated user's role and access.

## Categories

- Create categories
- View all categories
- View category by ID
- Update categories
- Deactivate categories
- Category validation
- Duplicate category validation

## Comments

- Add comments to tickets
- View ticket comments
- Update comments
- Delete comments
- Author-based access control
- Admin access
- Ticket access validation

## Attachments

- Upload files to tickets
- View attachment metadata
- Download attachments
- Delete attachments
- Ticket-based access validation
- File size restriction
- File type detection
- Secure file path handling
- Ticket-specific file storage

## Error Handling

The application provides standardized JSON error responses for:

- Validation errors
- Resource not found errors
- Duplicate user errors
- Invalid ticket status errors
- Unauthorized ticket access
- Bad requests
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `409 Conflict`

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| JJWT 0.12.6 | JWT implementation |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Application database |
| H2 | Test database |
| Maven | Build and dependency management |
| Lombok | Boilerplate reduction |
| Postman | API testing |
| Git | Version control |
| GitHub Actions | Continuous integration |

---

# Architecture

The application follows a layered architecture.

```text
                         Client
                           |
                           v
                   Spring Security
                           |
                           v
                     JWT Filter
                           |
                           v
                      Controller
                           |
                           v
                       Service
                           |
                           v
                     Repository
                           |
                           v
                      MySQL DB
```

### Request flow

```text
HTTP Request
     |
     v
Security Filter
     |
     +---- JWT validation
     |
     v
Controller
     |
     v
Service
     |
     v
Repository
     |
     v
Database
```

The service layer contains the main business logic, while repositories are responsible for database operations.

---

# Project Structure

```text
help-desk-api/
│
├── .github/
│   └── workflows/
│       └── maven.yml
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gokul/
│   │   │           └── help_desk_api/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               └── HelpDeskApiApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── gokul/
│       │           └── help_desk_api/
│       │               └── HelpDeskApiApplicationTests.java
│       │
│       └── resources/
│           └── application.properties
│
├── .gitignore
├── pom.xml
└── README.md
```

---

# Database Configuration

The application uses MySQL as the primary database.

Create the database:

```sql
CREATE DATABASE help_desk_db;
```

The application connects to:

```text
jdbc:mysql://localhost:3306/help_desk_db
```

Database configuration is supplied through environment variables rather than storing credentials directly in the repository.

---

# Application Configuration

The main application configuration uses environment variables for sensitive values.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/help_desk_db
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8082

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

file.upload-dir=uploads

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

Sensitive values such as database passwords and JWT secrets should not be committed to Git.

---

# Authentication

The API uses JWT-based authentication.

Authentication flow:

```text
Register
   |
   v
Login
   |
   v
JWT Token
   |
   v
Authorization Header
   |
   v
Protected API
```

After successful login, the API returns a JWT token.

The token must be sent with protected requests using:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# User Roles and Permissions

## ADMIN

Administrators have the highest level of access.

Typical administrative capabilities include:

- Manage categories
- Create and manage tickets
- Assign tickets
- Update ticket status
- Delete tickets
- Manage comments
- Access tickets across users
- Manage help desk operations

## IT_SUPPORT

IT Support users manage support tickets assigned to them.

Capabilities include:

- View assigned tickets
- Update assigned tickets
- Update ticket status
- Add comments
- View comments
- Upload attachments
- Manage ticket-related support activities

## EMPLOYEE

Employees can create and track their own support requests.

Capabilities include:

- Register and login
- Create tickets
- View their own tickets
- Add comments to accessible tickets
- Upload attachments to accessible tickets
- View ticket information they are authorized to access

Access is additionally enforced by service-layer business rules.

---

# API Endpoints

Base URL:

```text
http://localhost:8082
```

---

## Authentication Endpoints

### Register

```http
POST /api/auth/register
```

Example request:

```json
{
  "name": "Test Employee",
  "email": "employee@example.com",
  "password": "password123",
  "department": "IT"
}
```

### Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "email": "employee@example.com",
  "password": "password123"
}
```

The login response contains authentication information and the JWT token.

---

# Category Endpoints

## Create Category

```http
POST /api/categories
```

Requires:

```text
ADMIN
```

Example:

```json
{
  "name": "Network Support",
  "description": "Network, Wi-Fi and connectivity issues"
}
```

## Get All Categories

```http
GET /api/categories
```

## Get Category by ID

```http
GET /api/categories/{id}
```

## Update Category

```http
PUT /api/categories/{id}
```

Requires:

```text
ADMIN
```

## Deactivate Category

```http
PATCH /api/categories/{id}/deactivate
```

Requires:

```text
ADMIN
```

---

# Ticket Endpoints

## Create Ticket

```http
POST /api/tickets
```

Example:

```json
{
  "title": "Laptop is not powering on",
  "description": "My laptop does not power on when I press the power button.",
  "priority": "HIGH",
  "categoryId": 1
}
```

## Get Tickets

```http
GET /api/tickets
```

Returns tickets accessible to the authenticated user.

## Get Ticket by ID

```http
GET /api/tickets/{id}
```

## Update Ticket

```http
PUT /api/tickets/{id}
```

## Update Ticket Status

```http
PATCH /api/tickets/{id}/status
```

## Assign Ticket

```http
PATCH /api/tickets/{id}/assign
```

Requires appropriate administrative or IT Support permissions.

## Delete Ticket

```http
DELETE /api/tickets/{id}
```

Requires:

```text
ADMIN
```

---

# Comment Endpoints

## Add Comment

```http
POST /api/tickets/{ticketId}/comments
```

Example:

```json
{
  "comment": "The issue is being investigated by IT Support."
}
```

## Get Comments

```http
GET /api/tickets/{ticketId}/comments
```

## Update Comment

```http
PUT /api/tickets/{ticketId}/comments/{commentId}
```

## Delete Comment

```http
DELETE /api/tickets/{ticketId}/comments/{commentId}
```

Comment access is controlled based on the authenticated user's role, ticket access, and comment ownership.

---

# Attachment Endpoints

## Upload Attachment

```http
POST /api/tickets/{ticketId}/attachments
```

Use `multipart/form-data`.

Form field:

```text
file
```

Example:

```text
file = screenshot.png
```

## List Attachments

```http
GET /api/tickets/{ticketId}/attachments
```

## Download Attachment

```http
GET /api/tickets/{ticketId}/attachments/{attachmentId}
```

## Delete Attachment

```http
DELETE /api/tickets/{ticketId}/attachments/{attachmentId}
```

Attachment access is validated against the user's access to the ticket.

---

# Pagination, Filtering and Search

The API provides a dedicated paginated ticket endpoint:

```http
GET /api/tickets/paginated
```

Supported filters include:

- `status`
- `priority`
- `category`
- `createdBy`
- `assignedTo`
- `keyword`
- `page`
- `size`
- `sort`

Example:

```http
GET /api/tickets/paginated?page=0&size=10
```

Example with status:

```http
GET /api/tickets/paginated?status=OPEN
```

Example with priority:

```http
GET /api/tickets/paginated?priority=HIGH
```

Example with keyword:

```http
GET /api/tickets/paginated?keyword=laptop
```

Example with multiple filters:

```http
GET /api/tickets/paginated?status=OPEN&priority=HIGH&keyword=laptop&page=0&size=10
```

Keyword search supports case-insensitive matching across relevant ticket fields.

Tickets are sorted by creation time in descending order by default.

---

# Ticket Statistics

The statistics endpoint is:

```http
GET /api/tickets/statistics
```

The response provides ticket counts based on the authenticated user's access.

Example structure:

```json
{
  "totalTickets": 3,
  "openTickets": 1,
  "inProgressTickets": 1,
  "closedTickets": 1,
  "lowPriorityTickets": 1,
  "mediumPriorityTickets": 1,
  "highPriorityTickets": 1
}
```

The actual values depend on the current database contents.

---

# Ticket Status

The application supports the following ticket statuses:

```text
OPEN
IN_PROGRESS
ON_HOLD
RESOLVED
CLOSED
```

Typical workflow:

```text
OPEN
  |
  v
IN_PROGRESS
  |
  +----> ON_HOLD
  |
  v
RESOLVED
  |
  v
CLOSED
```

Status changes are controlled by the ticket service and security rules.

---

# Ticket Priority

The supported ticket priorities are:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Priority can be used for filtering and ticket management.

---

# Validation

Request DTOs use Jakarta Bean Validation.

Examples of validation rules include:

- Required fields
- Email validation
- Minimum and maximum field lengths
- Required ticket title
- Required ticket description
- Required priority
- Required category
- Category name validation

Example validation response:

```json
{
  "error": "Bad Request",
  "message": "title: Title is required; description: Description is required; priority: Priority is required; categoryId: Category is required",
  "path": "/api/tickets",
  "status": 400,
  "timestamp": "2026-10-06T09:48:01.203037"
}
```

---

# Exception Handling

The API uses a centralized exception handling mechanism.

The global exception handler provides consistent JSON responses for application errors.

Common response structure:

```json
{
  "timestamp": "2026-10-06T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/tickets/99"
}
```

Supported exception scenarios include:

- Resource not found
- User already exists
- Invalid ticket status
- Unauthorized ticket access
- Bad requests
- Validation failures
- Authentication failures
- Authorization failures

---

# Security

Spring Security is used to protect API endpoints.

Security features include:

- JWT authentication
- Stateless sessions
- Role-based authorization
- Protected API endpoints
- JWT authentication filter
- Custom authentication entry point
- Custom access denied handler
- Ticket ownership validation
- Ticket assignment validation
- Attachment access validation
- Comment access validation
- Secure file path handling

Unauthorized requests return:

```http
401 Unauthorized
```

Example:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required",
  "path": "/api/tickets"
}
```

Requests that are authenticated but don't have sufficient permissions return:

```http
403 Forbidden
```

Example:

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/categories"
}
```

---

# File Upload

Ticket attachments are stored on the server filesystem.

The default upload directory is:

```text
uploads/
```

Files are organized using ticket information.

The application also sanitizes file names and paths to reduce unsafe path handling.

Maximum file size:

```text
10MB
```

Configured using:

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

Uploaded files are excluded from Git using `.gitignore`.

---

# Postman Testing

The API can be tested using Postman.

## Step 1 — Register

Send:

```http
POST http://localhost:8082/api/auth/register
```

## Step 2 — Login

Send:

```http
POST http://localhost:8082/api/auth/login
```

Copy the JWT token from the response.

## Step 3 — Configure Authorization

For protected endpoints, use:

```text
Authorization
Bearer <JWT_TOKEN>
```

## Step 4 — Test APIs

You can then test:

```text
Categories
Tickets
Comments
Attachments
Pagination
Filtering
Search
Statistics
```

---

# Running the Application

## Prerequisites

Install:

- Java 17
- Maven
- MySQL
- Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

## 1. Clone the Repository

```bash
git clone https://github.com/Gokulgokul2001/help-desk-api.git
```

Move into the project:

```bash
cd help-desk-api
```

---

## 2. Create the Database

Open MySQL and run:

```sql
CREATE DATABASE help_desk_db;
```

---

## 3. Configure Environment Variables

Set:

```text
DB_PASSWORD
JWT_SECRET
```

For example, on Windows PowerShell:

```powershell
$env:DB_PASSWORD="your_database_password"
$env:JWT_SECRET="your_jwt_secret"
```

On Linux/macOS:

```bash
export DB_PASSWORD="your_database_password"
export JWT_SECRET="your_jwt_secret"
```

Do not commit real credentials to Git.

---

## 4. Build the Application

```bash
mvn clean package
```

---

## 5. Run the Application

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8082
```

---

# Maven Commands

## Clean Project

```bash
mvn clean
```

## Compile

```bash
mvn compile
```

## Run Tests

```bash
mvn test
```

## Run Full Verification

```bash
mvn clean verify
```

## Package Application

```bash
mvn clean package
```

## Run Spring Boot

```bash
mvn spring-boot:run
```

---

# Testing

The project uses Spring Boot testing support and H2 for test execution.

The production application uses MySQL, while automated tests use an in-memory H2 database.

Test configuration:

```text
src/test/resources/application.properties
```

This allows tests to run without requiring a MySQL server or production database credentials.

Run:

```bash
mvn clean verify
```

Expected result:

```text
BUILD SUCCESS
```

---

# GitHub Actions CI

The project includes a GitHub Actions workflow:

```text
.github/workflows/maven.yml
```

The workflow runs automatically when code is:

- Pushed to `main`
- Submitted as a pull request to `main`

The CI pipeline:

```text
Git Push
    |
    v
GitHub Actions
    |
    v
Checkout Repository
    |
    v
Set up Java 17
    |
    v
Restore Maven Cache
    |
    v
mvn clean verify
    |
    v
H2 Test Database
    |
    v
Build and Tests
```

The workflow uses:

```yaml
actions/checkout@v5
actions/setup-java@v5
```

Successful builds are displayed using the Maven Build badge at the top of this README.

---

# Environment Variables

The application expects the following environment variables:

| Variable | Description |
|---|---|
| `DB_PASSWORD` | MySQL database password |
| `JWT_SECRET` | Secret key used to sign JWT tokens |

Example:

```text
DB_PASSWORD=your_database_password
JWT_SECRET=your_secure_jwt_secret
```

Never commit actual secrets to the repository.

The following files are excluded from Git:

```text
.env
application-local.properties
application-local.yml
```

---

# Git Ignore

The repository excludes common generated and sensitive files, including:

```text
target/
.idea/
*.iml
*.log
logs/
uploads/
.env
application-local.properties
application-local.yml
*.class
```

This prevents build artifacts, IDE configuration, uploaded files, and local secrets from being committed.

---

# Future Improvements

Possible future enhancements include:

- Email notifications
- Ticket SLA management
- Ticket escalation
- Advanced reporting
- Dashboard APIs
- Audit logging
- User profile management
- Password reset
- Refresh tokens
- API documentation using OpenAPI / Swagger
- Docker support
- Production deployment
- Automated integration tests
- Database migration using Flyway or Liquibase
- More comprehensive unit and integration test coverage

---

# Author

**Gokul S**

GitHub:

https://github.com/Gokulgokul2001

---

# License

This project is intended for learning, portfolio, and demonstration purposes.