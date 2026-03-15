# Attendance Management System (Java Swing)

## 📌 Project Overview

The **Attendance Management System** is a desktop application developed using **Java Swing** that allows administrators to manage student records and track attendance.

The system provides separate access for **Admin** and **Students**.
Admins can add students, delete students, and mark attendance, while students can log in and view their attendance statistics.

This project demonstrates the use of **Object-Oriented Programming (OOP)** concepts, **Java GUI development**, and **file-based data persistence** using serialization.

---

## 🚀 Features

### Admin Features

* Secure admin login
* Add new students
* Delete student records
* Mark attendance for all students
* View student list with attendance percentage
* Color-coded attendance indicators

### Student Features

* Secure student login
* View subject-wise attendance
* View present hours and total hours
* View overall attendance percentage

### System Features

* Graphical User Interface (GUI) using Java Swing
* Attendance data stored using file serialization
* Automatic attendance percentage calculation
* Persistent data storage (data remains saved after closing the application)

---

## 🔑 Default Admin Credentials

To access the admin dashboard, use the following login details:

```
User ID: admin
Password: 1234
```

These credentials are defined directly in the program.

---

## 🧑‍🎓 Student Login

Students can log in using the **Student ID and password** that were created when the admin added them to the system.

---

## 🧠 Concepts Used

This project demonstrates the following programming concepts:

* Object-Oriented Programming (OOP)
* Inheritance
* Encapsulation
* Java Swing GUI
* Event Handling
* Collections Framework (ArrayList, HashMap)
* File Serialization
* Table Models in Swing
* Data Persistence

---

## 🏗 System Architecture

The project is implemented using multiple classes.

### Person (Parent Class)

Base class that stores common user attributes.

Attributes:

* ID
* Name
* Email
* Password

### Student (Child Class)

Extends the Person class.

Stores:

* Roll Number
* Department
* Year
* Attendance records

Functions:

* Add attendance
* Calculate attendance percentage

### Admin (Child Class)

Represents the administrator of the system.

Functions:

* Login authentication

### Attendance

Stores attendance details.

Attributes:

* Student ID
* Subject
* Date
* Status (Present/Absent)

### AttendanceManager

Handles all system operations.

Responsibilities:

* Add students
* Delete students
* Search students
* Mark attendance
* Save and load data

### AttendanceSystem

Main class that launches the application and manages the GUI.

Responsibilities:

* Login screen
* Admin dashboard
* Student dashboard

---

## 📊 Attendance Color Indicators

The system highlights attendance percentage using colors:

| Attendance % | Color  |
| ------------ | ------ |
| ≥ 90%        | Green  |
| 75% – 89%    | Yellow |
| < 75%        | Red    |

This helps quickly identify students with low attendance.

---

## 💾 Data Storage

The application stores data in a serialized file:

```
data.ser
```

This file contains:

* Student records
* Attendance records

Because of serialization, the data persists even after the application is closed.

---


## 🖥 Example Workflow

1. Admin logs in using default credentials.
2. Admin adds students to the system.
3. Admin marks attendance for a subject.
4. Students log in to view their attendance report.
5. System automatically calculates attendance percentage.

---

##  Possible Future Improvements

This project can be expanded by adding:

* Database integration (MySQL)
* Student registration system
* Attendance reports export (PDF/Excel)
* Graph-based attendance analytics
* Role-based authentication
* Web-based version using Spring Boot

---

##  Learning Outcomes

Through this project, the following skills were practiced:

* Designing object-oriented systems
* Creating GUI applications using Java Swing
* Managing collections in Java
* Implementing file-based data storage
* Handling user interactions in desktop applications

---

##  Author

Created as a **Java learning project** to practice:

* OOP concepts
* GUI development
* Attendance tracking system design
