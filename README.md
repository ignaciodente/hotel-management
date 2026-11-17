# Hotel Management

Hotel management system developed as the final project for Programming II.

## Features
- User login with role-based permissions (Administrator and Receptionist)
- Room booking with availability check by date range
- Guest check-in and check-out
- Lists of occupied, available and unavailable rooms
- Guest report with stay history
- Data backup to CSV files

## Tech stack
- Java
- JDBC
- MySQL

## Project structure
- `model` – domain classes (Person, Guest, User, Room, Reservation, Stay, Hotel)
- `enums` – enumerations
- `interfaces` – Identifiable and Repository
- `generics` – generic Registry class
- `exceptions` – custom exceptions
- `persistence` – database connection and repositories

## Team
- Maximiliano Fernandez – Part 1: Users
- Ignacio Dente – Part 2: Rooms and reservations
- Lucas de la O – Part 3: Hotel and registry
- Sami Musa Wassoof – Part 4: Persistence

## Class diagram

![Class diagram](docs/uml-hotel-management.png)