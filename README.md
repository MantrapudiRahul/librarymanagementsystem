Library Management System

A console-based Java application for managing library operations, built with Hibernate and MySQL. Admins can securely log in to manage books, register users, and track borrowing records via a menu-driven interface.
Features

Admin Login: Secure access with username/password (default: admin/admin123).
Book Management: Add, view, update, and delete books (title, author, ISBN, publication year, available copies).
User Management: Register and view library users (name, email, phone number).
Borrowing System: Borrow/return books with due date tracking.
Database Integration: Uses Hibernate ORM with MySQL for persistent storage.

Tech Stack

Language: Java 17
ORM: Hibernate 6.4.0
Database: MySQL 8.0+
Build Tool: Maven

Prerequisites

Java 17 (e.g., OpenJDK)
Maven 3.8+
MySQL 8.0+
Git
IDE (e.g., IntelliJ IDEA, Eclipse)

Setup Instructions

Clone the Repository:
git clone https://github.com/MantrapudiRahul/LibraryManagementSystem.git
cd LibraryManagementSystem


Configure MySQL:

Create a database named library_db:mysql -u your-username -p -e "CREATE DATABASE library_db;"


Update src/main/resources/hibernate.cfg.xml with your MySQL credentials:<property name="hibernate.connection.username">your-username</property>
<property name="hibernate.connection.password">your-password</property>




Load Initial Data:

Run src/main/resources/initial-data.sql to seed admin and sample data:mysql -u your-username -p library_db < src/main/resources/initial-data.sql




Build and Run:

Build the project:mvn clean install


Run the application:java -cp target/library-management-system-1.0-SNAPSHOT.jar com.example.library.Main




Usage:

Log in with default admin credentials: username: admin, password: admin123.
Use the console menu to manage books, users, and borrowings.




Project Structure
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.library
│   │   │       ├── entity
│   │   │       │   ├── Book.java
│   │   │       │   ├── User.java
│   │   │       │   ├── Borrow.java
│   │   │       │   └── Admin.java
│   │   │       ├── util
│   │   │       │   └── HibernateUtil.java
│   │   │       ├── dao
│   │   │       │   ├── BookDAO.java
│   │   │       │   ├── UserDAO.java
│   │   │       │   ├── BorrowDAO.java
│   │   │       │   └── AdminDAO.java
│   │   │       └── Main.java
│   │   ├── resources
│   │   │   ├── hibernate.cfg.xml
│   │   │   └── initial-data.sql
├── pom.xml
├── README.md
├── .gitignore
└── LICENSE

Troubleshooting

MySQL Connection Error:
Ensure MySQL is running: mysqladmin -u your-username -p status.
Verify credentials in hibernate.cfg.xml.


Hibernate Errors:
Check hibernate.show_sql=true logs in console.
Ensure initial-data.sql is applied.


Git Issues:
Fixed nested .git in librarymanagement and remote to LibraryManagementSystem.
Use PAT for authentication: GitHub > Settings > Developer settings > Personal access tokens.



Future Enhancements

Add password hashing for admin credentials.
Implement book search by title/author.
Support multiple admins.
Add input validation.

Contributing
Fork the repository, create a feature branch, and submit a pull request. For major changes, open an issue first.
License
MIT License - see LICENSE for details.
Contact

GitHub: MantrapudiRahul
Email: your-email@example.com

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
