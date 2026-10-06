# Help Desk API

A secure Spring Boot REST API for managing IT support tickets, users, categories, comments, and file attachments.

The application provides JWT-based authentication, role-based authorization, ticket assignment and status management, filtering, pagination, keyword search, statistics, validation, and standardized exception handling.

### User Roles

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
  - [Authentication Endpoints](#authentication-endpoints)
  - [Category Endpoints](#category-endpoints)
  - [Ticket Endpoints](#ticket-endpoints)
  - [Comment Endpoints](#comment-endpoints)
  - [Attachment Endpoints](#attachment-endpoints)
  - [Pagination, Filtering and Search](#pagination-filtering-and-search)
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
- [Environment Variables](#environment-variables)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

# Project Overview

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
- Lombok

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

The statistics endpoint provides ticket counts based on:

- Total tickets
- Open tickets
- In-progress tickets
- Resolved tickets
- Closed tickets
- Low priority
- Medium priority
- High priority

Statistics are scoped according to the authenticated user's role.

## Categories

- Create categories
- View all categories
- View category by ID
- Update categories
- Deactivate categories
- Category validation

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
- File storage management

## Error Handling

The application provides standardized JSON error responses for:

- Validation errors
- Resource not found errors
- Duplicate user errors
- Invalid ticket status errors
- Unauthorized ticket access errors
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
