# AmazingCo: Logistics Network Management System

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![JUnit](https://img.shields.io/badge/JUnit_4-25A162?style=for-the-badge&logo=junit5&logoColor=white)
  ![Coverage](https://img.shields.io/badge/Coverage-100%25-success?style=for-the-badge)
  ![Architecture](https://img.shields.io/badge/Architecture-OOP_Strict-blue?style=for-the-badge)
  ![Academic](https://img.shields.io/badge/Academic_Project-2nd_Year-purple?style=for-the-badge)

</div>

> **ABOUT THIS PROJECT:**
> This repository contains the complete implementation of an Object-Oriented Programming (OOP) system. It simulates a complex logistics and parcel delivery network for a fictional company called *AmazingCo*. It was made as the second practice of the subject OOP, first quarter of the second year.
> 
> The codebase enforces strict **SOLID principles**, extensive use of **Polymorphism**, and maintains a flawless **100% Test Coverage** ensuring zero dead code and high fault tolerance.

<p align="center">
  <img src="https://github.com/user-attachments/assets/a672db41-0533-48c0-8d94-f1b24a5d379d" alt="Cobertura de Tests 100%" width="75%" style="max-width:150%;" />
  <br>
  <br>
  <img src="https://github.com/user-attachments/assets/0bd0f42d-dd8b-45d8-9195-6b2cd63cb12a" alt="Vista Previa de Javadoc" width="75%" style="max-width:150%;" />
</p>

---


## System Architecture & Entities

The system routes packages to specific destinations based on geospatial coordinates (GPS), package type (Certified, Paid, Cash-on-Delivery), and point capacity.

### The Network Hierarchy
* **`PickingPointsSystem`:** The central orchestrator managing the entire network. Responsible for geospatial filtering and package eligibility routing.
* **`PickingPoint` (Abstract Base):** Defines the contract for all delivery locations.
  * **`PostOffice`:** The only entity authorized to handle highly secure `Certified` packages.
  * **`GroupablePoint` (Abstract):** Points that can be clustered together.
    * **`Kiosk`:** Manned points that accept *Cash-on-Delivery* packages and manage cash collections.
    * **`PackageLocker`:** Automated lockers with strict physical capacities.
  * **`PickingPointHub`:** A composite entity grouping multiple `GroupablePoints` together dynamically, sharing a single GPS coordinate.

### The Data Layer
* **`Package`:** The core data entity carrying tracking codes, payment status, expiry dates, and certified ID authorizations. Validates checksums internally.
* **`GPSCoordinate`:** Utility for geospatial calculations, utilizing the Vincenty formulae for ellipsoids to calculate precise Earth distances.

---

## Key Technical Features

- **Geospatial Filtering:** Calculates real-world distances between points to return eligible picking points within a specific radius.
- **Business Logic Routing:** Automatically filters delivery eligibility based on package criteria (e.g., Unpaid packages are strictly routed to points supporting Cash-on-Delivery).
- **Fault Tolerance:** Exhaustive input validation throwing precise `IllegalArgumentException`s for null states, duplicate identifiers, or logical impossibilities (e.g., overbooking a locker or retrieving an already picked-up package).
- **Test-Driven Development (TDD):** Built using a strict testing methodology.

---

## 100% Unit Test Coverage

This project features a massive, meticulously engineered test suite that covers **100% of the lines, branches, and conditions** across the critical path components. 

The test suite (`PickingPointsSystemTest`, `KioskTest`, `PickingPointHubTest`) validates:
* Every constructor, `if/else` branch, and logical short-circuit.
* Edge cases, Null Pointer defenses, and Exception handling.
* Overridden `equals()`, `hashCode()`, and `toString()` methods across the inheritance tree.
* Mocking of geographic anomalies and reflection hacks to verify unreachable defensive code.

---

## Getting Started

To run the system and verify the coverage locally:

1. Clone this repository: `git clone https://github.com/YourUsername/POO-Logistics-Network.git`
2. Import the project into your preferred Java IDE (Eclipse, IntelliJ IDEA, or VS Code).
3. Ensure **JUnit 4** is added to your Build Path.
4. Run the files inside the `test/` directory using your IDE's Test Runner.
5. Run with a Coverage tool to view the 100% branch execution.

---

## Authors
-Iván Moro Cienfuegos and Eric Soto San José
