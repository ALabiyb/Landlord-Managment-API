# Tanzania Landlord Management System - Project Overview

## 1. Project Purpose
The **Tanzania Landlord Management System** is a comprehensive backend application designed to help landlords in Tanzania manage their rental properties efficiently. It addresses local market needs by handling property listings, tenant management, lease agreements, and rent payments, all while adhering to local regulations and practices.

## 2. Architectural Design
The project follows a robust, modern architecture combining **Domain-Driven Design (DDD)** and **Clean Architecture** principles. This ensures the system is maintainable, testable, and scalable.

### Key Architectural Layers:

*   **Domain Layer (`domain`)**:
    *   **Core Business Logic:** Contains the heart of the application.
    *   **Entities & Aggregates:** `House`, `Room`, `Landlord`, `Tenant`, `Lease`. These objects encapsulate business rules and state.
    *   **Value Objects:** `Address`, `Money`, `HouseType`, `RoomStatus`. Immutable objects that describe characteristics.
    *   **Repository Interfaces:** Defines *what* data operations are needed, but not *how* they are implemented (e.g., `HouseRepository`).
    *   **Dependency Rule:** This layer has **zero dependencies** on frameworks, databases, or the web. It is pure Java business logic.

*   **Application Layer (`application`)**:
    *   **Use Cases / Services:** Orchestrates the flow of data to and from the domain entities (e.g., `HouseService`, `AuthService`).
    *   **DTOs (Data Transfer Objects):** Defines the structures for input (`CreateHouseRequest`) and output (`HouseResponse`) to decouple the API from the internal domain model.
    *   **Mappers:** Handles the conversion between DTOs and Domain objects.

*   **Infrastructure Layer (`infrastructure`)**:
    *   **Persistence:** Implements the repository interfaces defined in the Domain layer using **Spring Data JPA** and **Hibernate**.
    *   **Database:** Connects to a **PostgreSQL** database.
    *   **External Services:** Handles external concerns like PDF generation (iText) and email notifications (placeholder).

*   **API Layer (`api`)**:
    *   **REST Controllers:** Exposes the application's functionality via HTTP endpoints (e.g., `HouseController`, `AuthController`).
    *   **Documentation:** Uses **Swagger/OpenAPI** for automatic API documentation.
    *   **Security:** Handles authentication and authorization using **Spring Security** and **JWT**.

## 3. Features Implemented

### ✅ Authentication & Security
*   **Landlord Registration:** Secure registration with validation for Tanzanian phone numbers and National IDs.
*   **JWT Authentication:** Stateless authentication using JSON Web Tokens.
*   **Refresh Token Flow:** Ability to refresh expired access tokens without re-login.
*   **Password Reset:** Secure flow for "Forgot Password" and "Reset Password" functionality.
*   **Rate Limiting:** Protection against brute-force attacks on login and registration endpoints using Bucket4j.

### ✅ Property Management
*   **House Management:**
    *   Create, Update, Delete, and List houses.
    *   Support for various house types (Standalone, Apartment, etc.).
    *   Filtering houses by status (Vacant/Occupied).
*   **Room Management:**
    *   Add rooms to houses.
    *   Update room details and status (Vacant, Occupied, Maintenance).
    *   List rooms for a specific house.

### ✅ Dashboard & Analytics
*   **Dashboard Stats:** Real-time overview of:
    *   Total Properties & Rooms.
    *   Occupancy Rates (Occupied vs. Vacant).
    *   Total Tenants.
    *   Expected Monthly Income.

### ✅ Testing & Quality
*   **End-to-End (E2E) Tests:** Comprehensive integration tests using **Testcontainers** to verify the full system flow against a real PostgreSQL database.
*   **Unit Tests:** Isolated tests for core services (`AuthService`, `HouseService`, `RoomService`, `DashboardService`) using **Mockito**.
*   **Static Analysis:** Code quality checks using **SonarQube** rules.

## 4. Current Blockers & Missing Features

### 🚧 Immediate Technical Debt
*   **Database Migrations:** The project currently relies on Hibernate's `ddl-auto` feature. We need to integrate **Flyway** for professional, versioned database migrations to ensure safe schema updates in production.

### ⏳ Pending Features (Priority 2 & 3)
*   **Account Lockout:** Mechanism to lock accounts after multiple failed login attempts.
*   **Recent Activity Feed:** An endpoint to show a log of recent actions (payments, new tenants).
*   **Notifications:** A system for alerting landlords about expiring leases or overdue payments.
*   **Lease & Payment Management:** While the entities exist, the full service logic and endpoints for managing Leases and Payments need to be fully fleshed out and tested.
*   **Profile Management:** Endpoints for users to update their own profile details.

## 5. How to Run
1.  **Prerequisites:** Java 21, Maven, Docker.
2.  **Start Database:** `docker-compose up -d`
3.  **Run App:** `mvn spring-boot:run`
4.  **Run Tests:** `mvn test` (Requires Docker for Testcontainers)
5.  **API Docs:** Access Swagger UI at `http://localhost:8082/swagger-ui.html`
