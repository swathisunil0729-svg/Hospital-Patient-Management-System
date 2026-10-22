Hospital Patient Management System

Project Overview

The Hospital Patient Management System is a web-based application designed to organize patient records and manage waiting queues in a hospital. The project demonstrates the practical application of data structures to solve real-world healthcare management problems.
The system uses a Queue to manage regular patients according to the First-In-First-Out (FIFO) principle, a Priority Queue to handle emergency patients based on priority, and a Linked List concept to manage patient records dynamically.

Objectives
To maintain and organize patient records efficiently.
To manage regular consultation queues using FIFO.
To prioritize emergency patients based on their severity level.
To provide options to register, search, update, and remove patient records.
To demonstrate the use of data structures in a practical application.

Features
Patient Registration: Add new patients with their details.
Patient Record Management: Search, edit, view, and remove patient records.
Regular Patient Queue: Serve patients in arrival order.
Emergency Patient Management: Give higher-priority emergency cases precedence.
Queue Management: View waiting patients and serve the next patient.
User-Friendly Interface: Simple and responsive interface for easy navigation.

Technologies Used
HTML – Structure of the web interface.
CSS – Styling and responsive design.
JavaScript – Application logic and user interactions.
Java – Used for the data-structures-based console implementation.

Data Structures Used
Queue: Maintains the order of regular patients using FIFO.
Priority Queue: Arranges emergency patients according to their assigned priority.
Linked List: Supports dynamic storage and management of patient records.
How to Run the Project
Run the Web Version
Download or clone this repository.
Open the project folder.
Open index.html in a web browser.
Register patients and explore the available features.
Run the Java Version
Install the Java Development Kit (JDK).
Open a terminal in the folder containing HospitalPatientManagementSystem.java.

Compile the program:
javac HospitalPatientManagementSystem.java

Run the program:
java HospitalPatientManagementSystem
Live Demo
Visit the Hospital Patient Management System
GitHub Repository
View Source Code on GitHub

Limitations
Patient data in the web version is stored temporarily and may be lost when the page is refreshed.
The current project is a prototype and does not use a permanent database.
It is intended for educational purposes and is not a replacement for a real hospital management system.

Future Enhancement
Integrate a database for permanent patient record storage.
Add secure login for hospital staff.
Generate patient reports and appointment details.
Improve emergency prioritization with clinically appropriate criteria.
Connect the application to a backend server.

Conclusion
This project demonstrates how data structures can be used to improve patient record organization and queue management. It provides practical experience with Java, web technologies, and fundamental data structures while addressing a real-world hospital management problem.
