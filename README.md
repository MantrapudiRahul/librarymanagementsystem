# Library Management System

A Java-based application for managing a library's operations, built with **Spring Boot**, **Hibernate**, and **MySQL**. This system allows librarians to manage books, users, and borrowing records, with a RESTful API for seamless integration.

## Features
- **Book Management**: Add, update, view, and delete book records (title, author, ISBN, etc.).
- **User Management**: Register and manage library users (students, faculty, etc.).
- **Borrowing System**: Track book loans, returns, and due dates.
- **RESTful API**: Exposes endpoints for CRUD operations (e.g., `/api/books`, `/api/users`, `/api/borrows`).
- **Database Integration**: Uses Hibernate ORM with MySQL for persistent storage.
- **Search Functionality**: Search books by title, author, or ISBN.

## Tech Stack
- **Backend**: Java, Spring Boot, Hibernate
- **Database**: MySQL
- **Build Tool**: Maven
- **Testing**: Postman for API testing
- **Version Control**: Git

## Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+
- Git
- IDE (e.g., IntelliJ IDEA, Eclipse)

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/LibraryManagementSystem.git
   cd LibraryManagementSystem
