# RouteScout

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

RouteScout is an AI-powered travel assistant built with Spring Boot that combines Google's Gemini AI and Google Maps API to provide intelligent location suggestions, discover points of interest along your route, generate turn-by-turn driving directions, and find nearby amenities. Simply describe what you're looking for in natural language, and RouteScout will help you plan your journey with personalized recommendations.

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

#### POST /suggest/POIs

**Description**: Get AI-powered points of interest (POIs) along a route between two locations. This endpoint can find real places along your route, optionally filtered by a natural language query describing what you're looking for.

**URL**: `http://localhost:5001/suggest/POIs`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:
```json
{
  "origin": "Charleston, SC",
  "destination": "Columbia, SC",
  "query": "coffee shops"
}
```

**Request Parameters**:
- `origin` (required): Starting location (address, city, or landmark)
- `destination` (required): Ending location (address, city, or landmark)
- `query` (optional): Natural language description of what type of POI you're looking for

**Response** (Success - 200 OK):
```json
{
  "suggestions": [
    {
      "name": "Starbucks Coffee",
      "type": "cafe",
      "address": "1234 Highway 26, Summerville, SC 29485",
      "reason": "Popular coffee shop chain with comfortable seating and WiFi"
    },
    {
      "name": "The Daily Grind",
      "type": "cafe",
      "address": "5678 Main St, Orangeburg, SC 29115",
      "reason": "Local coffee shop known for artisan drinks and cozy atmosphere"
    }
  ]
}
```

**Response** (Error - 400 Bad Request):
```json
{
  "error": "Origin and Destination are required."
}
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/suggest/POIs`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "origin": "Daniel Island, SC",
     "destination": "James Island, SC",
     "query": "gas stations"
   }
   ```
6. Click Send

**cURL Example**:
```bash
curl -X POST http://localhost:5001/suggest/POIs \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "Charleston, SC",
    "destination": "Columbia, SC",
    "query": "restaurants"
  }'
```

**Example Queries**:
- "coffee shops" - Find coffee shops along your route
- "gas stations" - Locate gas stations for refueling
- "restaurants with outdoor seating" - Find dining options
- "parks" - Discover parks and rest areas
- Leave `query` empty for general AI-suggested POIs

**How It Works**:
- **With query**: The AI parses your natural language query into place types, then searches for real POIs along your route using Google Maps API
- **Without query**: Returns AI-generated POI suggestions based on the route

**Requirements**:
- Both `GOOGLE_API_KEY` (for AI parsing) and `GOOGLE_MAPS_API_KEY` (for POI search) must be configured
- Origin and destination should be valid location strings

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

### AmenityEndpoints - Nearby Places Search

RouteScout provides nearby places search functionality using Google's Places API (New). These endpoints allow you to find amenities like restaurants, gas stations, parks, and other points of interest near a specific location.

#### POST /nearest/amenity

**Description**: Find amenities near a given location. Returns detailed information about places including ratings, contact information, accessibility options, and restaurant-specific details.

**URL**: `http://localhost:5001/nearest/amenity`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:
```json
{
  "latitude": 32.7767,
  "longitude": -79.9309,
  "radius": 1500,
  "type": "restaurant"
}
```

**Request Parameters**:
- `latitude` (required): Latitude coordinate of the search center
- `longitude` (required): Longitude coordinate of the search center
- `radius` (optional): Search radius in meters (default: 1000, max: 50000)
- `type` (optional): Type of amenity to search for (e.g., "restaurant", "gas_station", "park", "cafe")
- `keyword` (optional): Search keyword (currently not implemented in search logic)

**Response** (Success - 200 OK):
```json
{
  "amenities": [
    {
      "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
      "name": "The Ordinary",
      "vicinity": "544 King Street, Charleston, SC 29403",
      "rating": 4.6,
      "userRatingsTotal": 1234,
      "latitude": 32.7876,
      "longitude": -79.9402,
      "types": ["restaurant", "seafood_restaurant", "bar"],
      "website": "https://www.theordinary.com",
      "phoneNumber": "(843) 414-7060",
      "openNow": true,
      "wheelchairAccessibleEntrance": true,
      "priceLevelString": "PRICE_LEVEL_EXPENSIVE",
      "hasRestroom": true,
      "dineIn": true,
      "takeout": false,
      "delivery": false,
      "servesBreakfast": false,
      "servesLunch": true,
      "servesDinner": true,
      "vegetarianFood": true,
      "reservable": true,
      "music": false
    }
  ],
  "error": null
}
```

**Response** (Error - 500 Internal Server Error):
```json
{
  "error": "Error fetching amenities: API request failed with response code: 403"
}
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/nearest/amenity`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "latitude": 32.7767,
     "longitude": -79.9309,
     "radius": 2000,
     "type": "cafe"
   }
   ```
6. Click Send

**cURL Example**:
```bash
curl -X POST http://localhost:5001/nearest/amenity \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 32.7767,
    "longitude": -79.9309,
    "radius": 1500,
    "type": "restaurant"
  }'
```

**Common Place Types**:
- `restaurant` - Restaurants
- `cafe` - Coffee shops and cafes
- `gas_station` - Gas stations
- `parking` - Parking facilities
- `park` - Parks and recreational areas
- `hospital` - Hospitals and medical facilities
- `pharmacy` - Pharmacies
- `grocery_or_supermarket` - Grocery stores
- `atm` - ATMs and banks
- `lodging` - Hotels and accommodations

For a complete list of supported types, see [Google Places API Types](https://developers.google.com/maps/documentation/places/web-service/place-types).

#### POST /nearest/amenity/types

**Description**: Search for multiple types of amenities near a location in a single request. This endpoint performs separate searches for each type and combines the results.

**URL**: `http://localhost:5001/nearest/amenity/types`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:
```json
{
  "latitude": 32.7767,
  "longitude": -79.9309,
  "radius": 1500,
  "types": ["restaurant", "cafe", "gas_station"]
}
```

**Request Parameters**:
- `latitude` (required): Latitude coordinate of the search center
- `longitude` (required): Longitude coordinate of the search center
- `radius` (optional): Search radius in meters (default: 1000)
- `types` (required): Array of place types to search for

**Response** (Success - 200 OK):
```json
{
  "amenities": [
    {
      "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
      "name": "The Ordinary",
      "vicinity": "544 King Street, Charleston, SC 29403",
      "rating": 4.6,
      "types": ["restaurant", "seafood_restaurant", "bar"],
      ...
    },
    {
      "placeId": "ChIJA2B3kTeuEmsRSomeOtherId",
      "name": "Kudu Coffee",
      "vicinity": "4 Vanderhorst Street, Charleston, SC 29403",
      "rating": 4.8,
      "types": ["cafe", "coffee_shop"],
      ...
    },
    {
      "placeId": "ChIJB3C4lTeuEmsRAnotherPlaceId",
      "name": "Shell Gas Station",
      "vicinity": "400 King Street, Charleston, SC 29403",
      "rating": 3.9,
      "types": ["gas_station", "convenience_store"],
      ...
    }
  ],
  "error": null
}
```

**Response** (Error - 500 Internal Server Error):
```json
{
  "error": "Error fetching amenities by types: API request failed with response code: 400"
}
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/nearest/amenity/types`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "latitude": 32.7767,
     "longitude": -79.9309,
     "radius": 2000,
     "types": ["restaurant", "cafe", "park"]
   }
   ```
6. Click Send

**cURL Example**:
```bash
curl -X POST http://localhost:5001/nearest/amenity/types \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 32.7767,
    "longitude": -79.9309,
    "radius": 1500,
    "types": ["restaurant", "cafe", "gas_station"]
  }'
```

**Use Cases**:
- Find all essential amenities (restaurants, gas stations, ATMs) near a rest stop
- Locate dining and entertainment options in a tourist area
- Search for multiple healthcare facilities (hospitals, pharmacies, clinics)

**Requirements**:
- The `GOOGLE_MAPS_API_KEY` must be configured in your environment variables or `application.yaml`
- Valid latitude and longitude coordinates
- The endpoint uses Google Places API (New) with the `searchNearby` method
- Maximum of 20 results per type (combined results may contain up to 20 × number of types)

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

### Github

Github project link: https://github.com/users/bmeudy11/projects/1
Github repository: https://github.com/bmeudy11/saasquatch.git

### Spring Boot

For further references with Spring Boot:

- [Spring Initializr](https://start.spring.io/)
- [Getting Started](https://spring.io/guides/gs/spring-boot/)

### Maven

For further references with Maven's dependency management framework:

- [Spring and Maven](https://spring.io/guides/gs/spring-boot/)
- [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
- [Apache Maven Getting Started](https://maven.apache.org/guides/getting-started/)

