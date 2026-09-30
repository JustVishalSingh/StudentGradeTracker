# Project Interview Guide

**1. What is this project and what problem does it solve?**
It is a "Student Academic Performance Management System". It replaces manual grade tracking with a structured desktop application that features role-based access, persistence in MySQL, and automated academic calculations (GPA, percentages, pass/fail).

**2. Why did you choose JavaFX?**
JavaFX is the modern standard for building rich desktop applications in Java. It enforces the MVC (Model-View-Controller) design pattern and allows styling with CSS, making it much more professional than older frameworks like Swing.

**3. How did you implement OOP concepts in this project?**
- **Encapsulation**: All class fields are private with public getters and setters to protect data integrity.
- **Inheritance & Abstraction**: Created an abstract `Person` class that contains shared attributes (name, email) which `Student` and `Teacher` inherit from.
- **Composition**: The `Student` class contains a reference (`userId`) to a `User` account, representing a "has-a" relationship mapping to our relational database.

**4. Why use Maven instead of compiling manually?**
Maven handles dependency management (like MySQL Connector and JavaFX) automatically. It also standardizes the build process (`mvn clean package`), making the project easy to share and build on any computer without manual configuration.

**5. How did you secure the database connections?**
I used a `config.properties` file located in the resources folder rather than hardcoding credentials in Java classes. I also use `PreparedStatement` for all JDBC queries to prevent SQL Injection attacks.
