# RouteScout - Iteration 1

## Overview

RouteScout is a Spring Boot REST API application designed for route planning and user management. This document describes the features and functionality implemented in **Iteration 1**.

## Iteration 1 Features

### User Account Management

Iteration 1 establishes the foundation of the RouteScout application with a complete user account management system:

- **Account Creation**: Users can create new accounts with username, email, and password
- **Account Retrieval**: Accounts can be retrieved by ID or username
- **Data Persistence**: All account data is stored in a PostgreSQL database
- **Database Migrations**: Automated database schema management using Flyway

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.3.2
- **Database**: PostgreSQL
- **Database Migration**: Flyway 10.17.1
- **ORM**: Hibernate 6.5.2
- **Build Tool**: Maven
- **API Documentation**: Swagger/OpenAPI (SpringDoc 2.6.0)

## Database Schema

### Accounts Table

The `accounts` table stores user account information:

| Column      | Type         | Constraints           | Description                    |
|-------------|--------------|------------------------|--------------------------------|
| user_id     | SERIAL       | PRIMARY KEY           | Unique identifier              |
| username    | VARCHAR(50)  | UNIQUE, NOT NULL      | Unique username                |
| password    | VARCHAR(50)  | NOT NULL              | User password                  |
| email       | VARCHAR(255) | UNIQUE, NOT NULL      | User email address             |
| created_on  | TIMESTAMP    | NOT NULL, DEFAULT now()| Account creation timestamp    |
| last_login  | TIMESTAMP    | NOT NULL, DEFAULT now()| Last login timestamp          |

## API Endpoints

### Account Management

All account endpoints are prefixed with `/account`.

#### Create Account
- **Method**: `POST`
- **Path**: `/account`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "username": "johndoe",
    "password": "securepassword",
    "email": "john@example.com"
  }
  ```
- **Response**: `201 CREATED`
  ```json
  {
    "user_id": 1,
    "username": "johndoe",
    "password": "securepassword",
    "email": "john@example.com",
    "created_on": "2025-10-30T23:00:00.000+00:00",
    "last_login": "2025-10-30T23:00:00.000+00:00"
  }
  ```

#### Get Account by Username
- **Method**: `GET`
- **Path**: `/account/username/{username}`
- **Response**: `200 OK` or `404 NOT FOUND`
  ```json
  {
    "user_id": 1,
    "username": "johndoe",
    "password": "securepassword",
    "email": "john@example.com",
    "created_on": "2025-10-30T23:00:00.000+00:00",
    "last_login": "2025-10-30T23:00:00.000+00:00"
  }
  ```

#### Get Account by ID
- **Method**: `GET`
- **Path**: `/account/{id}`
- **Response**: `200 OK` or `404 NOT FOUND`
  ```json
  {
    "user_id": 1,
    "username": "johndoe",
    "password": "securepassword",
    "email": "john@example.com",
    "created_on": "2025-10-30T23:00:00.000+00:00",
    "last_login": "2025-10-30T23:00:00.000+00:00"
  }
  ```

### Health Check
- **Method**: `GET`
- **Path**: `/status`
- **Response**: `200 OK` - Returns application status

## Setup and Installation

### Prerequisites

1. **Java JDK 17 or higher**
   - Download from [AWS Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
   - Or install via Homebrew (Mac): `brew install --cask corretto@17`

2. **PostgreSQL Database**
   - Ensure you have a PostgreSQL instance running
   - Update database credentials in `src/main/resources/application.yaml`

### Configuration

Update the database connection settings in `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-database-host:5432/your-database-name
    username: your-username
    password: your-password
```

### Build the Project

```bash
./mvnw clean install
```

On Windows:
```bash
.\mvnw clean install
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port **5001**.

### Access the API

- **Swagger UI**: [http://localhost:5001/swagger-ui/index.html](http://localhost:5001/swagger-ui/index.html)
- **API Base URL**: [http://localhost:5001](http://localhost:5001)

## Project Structure

```
src/
├── main/
│   ├── java/edu/citadel/
│   │   ├── api/                    # REST Controllers
│   │   │   ├── AccountEndpoints.java
│   │   │   ├── StatusEndpoints.java
│   │   │   └── request/            # Request DTOs
│   │   ├── config/                 # Application Configuration
│   │   ├── dal/                    # Data Access Layer
│   │   │   ├── AccountRepository.java
│   │   │   └── model/              # JPA Entities
│   │   │       └── Account.java
│   │   └── main/                   # Application Entry Point
│   │       └── RouteScoutApplication.java
│   └── resources/
│       ├── application.yaml        # Application Configuration
│       └── db/migration/           # Flyway Database Migrations
│           └── V1__create_accounts_table.sql
└── test/
    └── java/edu/citadel/main/
        └── RouteScoutTests.java    # Unit Tests
```

## Testing

Run the test suite:

```bash
./mvnw test
```

## What's Next?

Future iterations will include:
- Route planning features
- Authentication and authorization
- Password encryption
- Additional user management features (update, delete)
- Route sharing and collaboration
- Geographic data integration

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on contributing to this project.

## License

This project is licensed under the MIT License. See [LICENSE.md](LICENSE.md) for details.
