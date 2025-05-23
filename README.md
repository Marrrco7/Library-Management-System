﻿# Library-Management-System




## Fully functioning Library Management desktop application to:

- Allow librarians to add, edit, delete, and view Users, Books, Copies, Borrowings, Librarians, and Publishers via a Swing GUI.  
- Let regular users view the complete catalog, check available titles, and consult their own borrowing history.  
- Persist data using JPA annotations and Hibernate, modeling one-to-many, many-to-one, and one-to-one relationships.  
- Ensure all entities support full CRUD, with delete operations restricted when referential constraints exist.  
- Provide comprehensive unit tests (JUnit 5) and integration tests (H2 in-memory) covering CRUD operations, relationships, business logic (e.g., borrowing limits, concurrency), and edge cases.  
- Document all classes and methods with JavaDocs and publish as an HTML package.

---

##  Features

- **User Management** – create, read, update, delete library users  
- **Book & Copy Management** – handle multiple copies per title, with status tracking  
- **Borrowing Workflow** – register borrow/return events; enforce concurrency and business rules  
- **Librarian Roles** – special user type with elevated privileges  
- **Publisher Directory** – manage publisher details and link books to publishers  
- **Swing GUI** – intuitive forms & tables for all operations, with role-based views  
- **Robust Testing** –  
  - Unit tests for each entity’s CRUD  
  - Relationship tests (One-to-Many, One-to-One, Many-to-One)  
  - Edge-case & concurrency tests  
- **Auto-generated Docs** – JavaDocs for every class and method, plus setup instructions

---

## Technology Stack

- **Language & UI**: Java 22, Swing  
- **Persistence**: JPA (Hibernate implementation)  
- **Database**: H2 (test), MySQL/PostgreSQL for production in the future
- **Build & Dependency Management**: Maven  
- **Testing**: JUnit 5  
- **Documentation**: JavaDocs

![image](https://github.com/user-attachments/assets/27f4c5a3-14ca-4161-a820-1434e4cc51a2)

---
