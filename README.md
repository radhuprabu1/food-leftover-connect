# Food Leftover Connect - Backend Platform

[](https://www.java.com/)
[](https://spring.io/projects/spring-boot)
[](https://maven.apache.org/)
[](https://www.google.com/search?q=%23)
[](https://www.postman.com/)

## 📋 Project Overview

This is a robust, event-driven backend for **Food Leftover Connect**, a platform designed to connect food donors with receivers like NGOs and volunteers in real-time.

Built using **Java 17** and **Spring Boot**, this application follows a **modular, microservice-ready architecture** to ensure scalability and maintainability. It uses an in-memory H2 database for development and is designed for high performance and resilience.

-----

## 🛠️ Technologies Used (Tech Stack)

  - ⚙️ Java 17
  - 🌱 Spring Boot 3.x
  - 📦 Maven (Multi-Module Project)
  - 💾 Spring Data JPA with H2 (for data persistence)
  - 🔄 Asynchronous, Event-Driven Communication
  - 🧪 JUnit & Mockito (for unit testing)
  - 🧰 Eclipse STS (IDE)
  - 🧪 Postman (for API testing)

-----

## 🚀 Main Features

  - 👤 **User Management**: Separate registration for **Donors** and **Receivers** with distinct roles.
  - 🍲 **Food Listing**: Donors can list available food donations with details like quantity, prepared time, pickup time, and expiry.
  - 🗺️ **Location-Based Matching**: Automatically finds and alerts nearby receivers (within a 5km radius) when a new food listing is created.
  - ✅ **Acceptance Flow**: Receivers can accept a food listing. Once accepted, the listing is locked and cannot be accepted by others.
  - ⏳ **Reminders**: Receivers can request a "Remind Later" notification for a food listing, which is triggered by a scheduled background job.
  - ❌ **Cancellation Logic**:
      - Donors can cancel a listing if it's not yet accepted or if the pickup time is not imminent.
      - Receivers can cancel their acceptance, making the food listing available again for others.
  - ✔️ **Collection Confirmation**: Receivers can mark a food item as "collected," completing the donation lifecycle.
  - 🧹 **Automated Cleanup**: A daily scheduled job cleans up expired and collected listings from the database to maintain system hygiene.
  - 👑 **Admin Oversight**: Endpoints for an `Admin` role to view all users, donors, receivers, and food listings in the system.

-----

## 📚 API Endpoints

|HTTP Method| Endpoint | Purpose |
| :--- | :--- | :--- |
| `POST` | `/api/users/register` | Creates a new Donor or Receiver. |
| `POST` | `/api/food-listings` | A Donor lists a new food donation. |
| `GET` | `/api/receivers/listings/available` | A Receiver gets nearby, available listings. |
| `POST` | `/api/receivers/responses/{foodListingId}` | A Receiver accepts, rejects, or sets a reminder. |
| `POST` | `/api/receivers/listings/{foodListingId}/collect` | A Receiver confirms collection of the food. |
| `DELETE` | `/api/receivers/listings/{foodListingId}/acceptance` | A Receiver cancels their acceptance. |
| `GET` | `/api/donors/nearby-receivers` | A Donor requests a list of nearby receivers. |
| `GET` | `/api/admin/users` | An Admin views all users. |
| `GET` | `/api/admin/food-listings` | An Admin views all food listings. |

-----

## 🧩 Project Structure

```
com.app.foodleftoverconnect
├── food-service     // Manages donors and food listing logic
├── receiver-service  // Manages receivers, reminders, and matching logic
├── user-service      // Manages user registration and is the source of truth for user data
├── notification-service // Manages sending notifications
├── api-gateway       // The single entry point for all API requests
├── app-main          // Aggregates and runs the services for local development
└── common-lib        // Shared DTOs, events, and constants
```

-----

## 👨‍💻 How to Run the Project

1.  Clone the repository.
2.  Ensure you have Java 17 and Maven installed.
3.  Start the applications in order:
      - Run `MainApplication.java` from the `app-main` module (starts services on port 8081).
      - Run `ApiGatewayApplication.java` from the `api-gateway` module (starts gateway on port 8080).
4.  All API requests should be sent to the gateway at `http://localhost:8080`.

-----

## 📌 Future Enhancements

  - 🛡️ **Implement API Gateway & Spring Security**: Transition from the current `X-User-Id` header to a full, production-grade security implementation using **JWTs** for stateless authentication and role-based authorization.
  - 🌐 **WebSocket Integration**: Implement a WebSocket layer to push real-time updates (like new food listings or search results) directly to the frontend for a dynamic user experience.
  - 🐳 **Containerization with Docker**: Package each service and the API Gateway into separate Docker containers for consistent and scalable deployments.
  - 🔄 **CI/CD Pipeline**: Create an automated CI/CD pipeline (e.g., with GitHub Actions) to test and deploy the application.
  - ⭐ **Rating & Review System**: Allow donors and receivers to rate each other after a completed donation.
  - 💬 **Real-time Chat**: Implement a simple chat feature between a donor and a receiver after a listing is accepted to coordinate pickup.

-----

⭐️ **If you like this project, please give it a star on GitHub to show support\!**
