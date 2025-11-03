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
| `GOOGLE_MAPS_API_KEY` | Google Maps API key for route generation and directions | `AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX` |

### Setting Environment Variables

**Mac/Linux**:
```bash
export GOOGLE_API_KEY=your_api_key_here
export GOOGLE_MAPS_API_KEY=your_maps_api_key_here
```

**Windows (Command Prompt)**:
```cmd
set GOOGLE_API_KEY=your_api_key_here
set GOOGLE_MAPS_API_KEY=your_maps_api_key_here
```

**Windows (PowerShell)**:
```powershell
$env:GOOGLE_API_KEY="your_api_key_here"
$env:GOOGLE_MAPS_API_KEY="your_maps_api_key_here"
```

Alternatively, you can set the `GOOGLE_MAPS_API_KEY` directly in the configuration files:
- `src/main/resources/application.yaml` (for production/development)
- `src/main/resources/application-test.yaml` (for testing)

In either file, update line 19:
```yaml
google:
  maps:
    key: ${GOOGLE_MAPS_API_KEY:your_maps_api_key_here}
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

## API Endpoints

### AIEndpoints - Location Suggestions

RouteScout provides AI-powered location suggestions through the `/suggest` endpoint.

#### GET /suggest

**Description**: Health check endpoint to verify the service is available.

**URL**: `http://localhost:5001/suggest`

**Method**: `GET`

**Response**:
```json
{
  "status": "Service available"
}
```

**Postman Setup**:
1. Create a new GET request
2. Enter URL: `http://localhost:5001/suggest`
3. Click Send

#### POST /suggest

**Description**: Get AI-powered location suggestions based on natural language queries.

**URL**: `http://localhost:5001/suggest`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:
```json
{
  "message": "Find me a quiet coffee shop to study"
}
```

**Response** (Success - 200 OK):
```json
{
  "suggestions": [
    {
      "name": "The Study Cafe",
      "type": "Cafe",
      "address": "123 Main Street, Charleston, SC",
      "reason": "Quiet atmosphere with comfortable seating, free WiFi, and outlets at every table."
    },
    {
      "name": "Bookworm Coffee House",
      "type": "Cafe",
      "address": "456 King Street, Charleston, SC",
      "reason": "Library-inspired decor with private study nooks and a strict quiet policy after 2pm."
    }
  ]
}
```

**Response** (Error - 400 Bad Request):
```json
{
  "error": "Message is required."
}
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/suggest`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "message": "Find me a coffee shop on a route between Daniel Island, SC and James Island, SC"
   }
   ```
6. Click Send

**Example Queries**:
- "Find me a coffee shop with WiFi"
- "I need a quiet place to study"
- "Suggest parks for a family picnic"
- "Looking for restaurants with outdoor seating"

### RouteEndpoints - Route Generation

RouteScout provides route generation and turn-by-turn directions using Google Maps API.

#### POST /route/generateRoute

**Description**: Generate a driving route with step-by-step directions between two locations.

**URL**: `http://localhost:5001/route/generateRoute`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:
```json
{
  "origin": "Charleston, SC",
  "destination": "Columbia, SC"
}
```

**Response** (Success - 200 OK):
```json
{
  "origin": "Charleston, SC",
  "destination": "Columbia, SC",
  "distance": "114 mi",
  "duration": "1 hour 53 mins",
  "instructions": [
    "Head northwest on Meeting St toward Cumberland St.",
    "Turn right onto Spring St.",
    "Turn left onto US-52 N/Morrison Dr.",
    "..."
  ]
}
```

**Response** (Error - 500 Internal Server Error):
```json
"Error message describing what went wrong"
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/route/generateRoute`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "origin": "Daniel Island, SC",
     "destination": "James Island, SC"
   }
   ```
6. Click Send

**cURL Example**:
```bash
curl -X POST http://localhost:5001/route/generateRoute \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "Charleston, SC",
    "destination": "Columbia, SC"
  }'
```

**Requirements**:
- The `GOOGLE_MAPS_API_KEY` must be set in `application.yaml` or `application-test.yaml`
- Both `origin` and `destination` should be valid location strings (addresses, cities, or landmarks)
- The endpoint uses Google Maps Directions API for driving routes

### Health Check Endpoint

#### GET /actuator/health

**Description**: Spring Boot Actuator health check endpoint to verify the application is running.

**URL**: `http://localhost:5001/actuator/health`

**Method**: `GET`

**Response** (Success - 200 OK):
```json
{
  "status": "UP"
}
```

**Postman Setup**:
1. Create a new GET request
2. Enter URL: `http://localhost:5001/actuator/health`
3. Click Send

**cURL Example**:
```bash
curl http://localhost:5001/actuator/health
```

**Note**: The health endpoint is enabled in `application.yaml` under the `management.endpoint.health.enabled` configuration.

## Running Tests

### Running All Tests

To run all tests in the project:

```bash
./mvnw test
```

> Note: On Windows, replace `./mvnw` with `.\mvnw`

### Running RouteScoutAgent Tests

To run only the RouteScoutAgent test suite:

```bash
./mvnw test -Dtest=RouteScoutAgentTest
```

This will execute all unit tests for the RouteScoutAgent class, including:
- Constructor initialization tests
- Input validation tests
- Error handling tests
- Prompt format validation tests

### Running Specific Tests

To run a specific test method:

```bash
./mvnw test -Dtest=RouteScoutAgentTest#testGetSuggestions_WithValidMessage
```

### Test Output

After running tests, you can view:
- **Console output**: Test results displayed in the terminal
- **HTML Report**: Generated at `target/surefire-reports/index.html`
- **XML Reports**: Available in `target/surefire-reports/` directory

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

