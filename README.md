# RouteScout

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

RouteScout is a Spring Boot application that uses Google's Gemini AI to provide intelligent location suggestions based on natural language queries for travel routes.

## Prerequisites

Before setting up RouteScout, ensure you have the following installed:

### Required Software

1. **Java JDK 17+**: RouteScout requires Java Development Kit 17 or higher
    - **Mac**: Install using Homebrew
      ```bash
      brew install --cask corretto@17
      ```
    - **Windows**: Download the `.msi` installer from [AWS Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) and run it
    - **Verify installation**:
      ```bash
      java -version
      ```

2. **Maven**: Included via Maven Wrapper (`mvnw`), no separate installation needed

3. **PostgreSQL Database**: Required for data persistence
    - Install PostgreSQL 12 or higher
    - Create a database for the application

4. **Google API Key**: Required for Gemini AI integration
    - Obtain an API key from [Google AI Studio](https://makersuite.google.com/app/apikey)

## Environment Variables

RouteScout requires the following environment variables to be set:

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `GOOGLE_API_KEY` | Google Gemini API key for AI-powered location suggestions | `AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX` |

### Setting Environment Variables

**Mac/Linux**:
```bash
export GOOGLE_API_KEY=your_api_key_here
```

**Windows (Command Prompt)**:
```cmd
set GOOGLE_API_KEY=your_api_key_here
```

**Windows (PowerShell)**:
```powershell
$env:GOOGLE_API_KEY="your_api_key_here"
```

Alternatively, you can create an `application.properties` file in `src/main/resources/` with:
```properties
GOOGLE_API_KEY=your_api_key_here
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd saasquatch
```

### 2. Set Up Environment Variables

Set the required `GOOGLE_API_KEY` environment variable as described above.

### 3. Build the Project

> Note: On Windows, replace `./mvnw` with `.\mvnw`

```bash
./mvnw clean install
```

You should see a `BUILD SUCCESS` message if everything is set up correctly.

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port `5001`.

### 5. Access the API

Once the application is running, you can access:

- **Swagger UI**: [http://localhost:5001/swagger-ui/index.html](http://localhost:5001/swagger-ui/index.html)
    - Interactive API documentation where you can test endpoints directly

### 6. Success!

You should now have RouteScout running locally!

## Setting Up DataSource

Setting up the datasource within IntelliJ should be straightforward with username and password. If you're using a
Heroku datasource then you will need to set the following settings on the Advanced tab

![Datasource](./images/datasource_settings.png)

## Resources

### Spring Boot

For further references with Spring Boot:

- [Spring Initializr](https://start.spring.io/)
- [Getting Started](https://spring.io/guides/gs/spring-boot/)

### Maven

For further references with Maven's dependency management framework:

- [Spring and Maven](https://spring.io/guides/gs/spring-boot/)
- [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
- [Apache Maven Getting Started](https://maven.apache.org/guides/getting-started/)

