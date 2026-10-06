# Help Desk API

A secure Spring Boot REST API for managing IT support tickets, users, categories, comments, and ticket attachments.

The application provides JWT-based authentication and role-based authorization for three types of users:

- `ADMIN`
- `IT_SUPPORT`
- `EMPLOYEE`

Employees can create and track IT support tickets, IT Support users can manage assigned tickets, and administrators can manage the overall help desk system.

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
    - [Authentication](#authentication-endpoints)
    - [Categories](#category-endpoints)
    - [Tickets](#ticket-endpoints)
    - [Ticket Comments](#comment-endpoints)
    - [Ticket Attachments](#attachment-endpoints)
    - [Pagination and Filtering](#pagination-filtering-and-search)
    - [Statistics](#ticket-statistics)
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
- [Future Improvements](#future-improvements)
- [Author](#author)

---

# Project Overview

## Help Desk API

The Help Desk API is a backend REST API designed for managing internal IT support requests.

The system allows employees to create support tickets and enables IT Support staff to manage tickets assigned to them.

Administrators have full access to the system and can manage categories, tickets, assignments, and other administrative operations.

The application uses:

- Spring Boot
- Spring Security
- JWT authentication
- Spring Data JPA
- Hibernate
- MySQL
- Maven

---

# Features

## Authentication

- User registration
- User login
- JWT token generation
- JWT token validation
- Stateless authentication
- Protected API endpoints

## Authorization

Three roles are supported:

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

- Keyword search
- Status filtering
- Priority filtering
- Category filtering
- Created-by filtering
- Assigned-to filtering
- Pagination
- Sorting

## Ticket Statistics

- Total tickets
- Open tickets
- In-progress tickets
- Resolved tickets
- Closed tickets
- Priority statistics

## Categories

- Create categories
- View categories
- View category by ID
- Update categories
- Deactivate categories

## Comments

- Add comments
- View comments
- Update comments
- Delete comments
- Author-based access control
- Admin access

## Attachments

- Upload files
- View attachment metadata
- Download attachments
- Delete attachments
- Ticket-based access validation
- File size restriction
- File storage management

## Error Handling

- Validation errors
- Resource not found errors
- Duplicate user errors
- Invalid ticket status errors
- Unauthorized ticket access errors
- 401 Unauthorized handling
- 403 Forbidden handling
- Standardized JSON error responses

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Database |
| Maven | Build and dependency management |
| Lombok | Boilerplate reduction |
| Postman | API testing |

---

# Architecture

The application follows a layered architecture.

```text
                    Client
                      |
                      v
              Spring Security
                      |
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