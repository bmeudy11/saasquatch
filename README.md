 # RouteScout

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

RouteScout is an AI-powered travel assistant built with Spring Boot that combines Google's Gemini AI and Google Maps API to provide intelligent location suggestions, discover points of interest along your route, generate turn-by-turn driving directions, and find nearby amenities. Simply describe what you're looking for in natural language, and RouteScout will help you plan your journey with personalized recommendations.

## Project Structure

RouteScout is a full-stack application consisting of:

- **Backend**: Spring Boot REST API (port 5001)
  - AI-powered location suggestions using Google Gemini
  - Route generation with Google Maps API
  - Nearby places search functionality

- **Frontend**: React UI (port 3000)
  - User interface for interacting with the RouteScout API
  - Located in `ui/saasquatch-ui/`
  - See [UI README](ui/saasquatch-ui/README.md) for detailed frontend setup

## Prerequisites

Before setting up RouteScout, ensure you have the following installed:

### Required Software

#### Backend Requirements

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

#### Frontend Requirements

1. **Node.js 14+**: Required for running the React UI
    - **Mac**: Install using Homebrew
      ```bash
      brew install node
      ```
    - **Windows**: Download the installer from [nodejs.org](https://nodejs.org/)
    - **Verify installation**:
      ```bash
      node -v
      npm -v
      ```
      
## API Configuration

RouteScout requires API keys for Google Gemini and Google Maps services. These should be configured directly in the application configuration files.

### Required API Keys

| Key | Description | How to Obtain | Required APIs                                         |
|-----|-------------|---------------|-------------------------------------------------------|
| Google API Key | Google Gemini API key for AI-powered location suggestions | [Google AI Studio](https://makersuite.google.com/app/apikey) | Gemini API                                            |
| Google Maps API Key | Google Maps API key for route generation, directions, and map visualization | [Google Cloud Console](https://console.cloud.google.com/) | Directions API, Places API (New), Maps JavaScript API |

### Setting API Keys

Configure your API keys in the YAML configuration files:

**For Production/Development**:
Edit `src/main/resources/application.yaml`:
```yaml
google:
  api:
    key: your_google_api_key_here
  maps:
    key: your_google_maps_api_key_here
```

**For Testing**:
Edit `src/main/resources/application-test.yaml`:
```yaml
google:
  api:
    key: your_google_api_key_here
  maps:
    key: your_google_maps_api_key_here
```

**For Frontend (React UI)**:
Create or edit `ui/saasquatch-ui/.env`:
```env
REACT_APP_BASE_URL=http://localhost:5001
REACT_APP_GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
```

**Important Notes**:
- The frontend `REACT_APP_GOOGLE_MAPS_API_KEY` should use the same Google Maps API key as the backend
- Ensure **Maps JavaScript API** is enabled in your Google Cloud Console for map visualization to work
- The frontend API key is used for client-side map rendering with the `@react-google-maps/api` library

**Security Note**: Never commit API keys to version control. Add these configuration files to `.gitignore` or use environment-specific configurations.

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd saasquatch
```

### 2. Configure API Keys

Configure your Google API keys in the configuration files as described in the [API Configuration](#api-configuration) section above.

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

## Running the Frontend

The RouteScout React UI provides a user-friendly interface for interacting with the backend API, including Google Maps visualization for routes.

### 1. Navigate to the UI Directory

```bash
cd ui/saasquatch-ui
```

### 2. Install Dependencies

```bash
npm install
```

This will install all required packages including:
- React and core dependencies
- `@react-google-maps/api` for Google Maps integration
- Axios for API calls
- SASS for styling

### 3. Configure Environment Variables

Create a `.env` file in the `ui/saasquatch-ui/` directory with your API keys:

```env
REACT_APP_BASE_URL=http://localhost:5001
REACT_APP_GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
```

See the [API Configuration](#api-configuration) section for details on obtaining API keys.

### 4. Start the Development Server

```bash
npm start
```

The UI will start on port `3000` and automatically open in your browser at [http://localhost:3000](http://localhost:3000).

### 5. Connecting to the Backend

Ensure the backend is running on port `5001` before using the UI. The frontend will make API calls to `http://localhost:5001`.

### Additional UI Information

For more detailed information about the UI, including available scripts and configuration options, see the [UI README](ui/saasquatch-ui/README.md).

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
- Both `GOOGLE_API_KEY` (for AI parsing) and `GOOGLE_MAPS_API_KEY` (for POI search) must be configured in `application.yaml` or `application-test.yaml`
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

### DestinationEndpoints - Random Destination Generator

RouteScout can generate a random destination near your location and provide driving directions to get there.

#### POST /destination/generateDestination

**Description**: Generate a random destination within a specified radius from your starting point and receive turn-by-turn directions to get there. The endpoint randomly selects from various destination types including restaurants, cafes, bakeries, amusement centers, cultural centers, movie theaters, parks, and cultural landmarks.

**URL**: `http://localhost:5001/destination/generateDestination`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:
```json
{
  "origin": "Charleston, SC",
  "radius": 5000
}
```

**Request Parameters**:
- `origin` (required): Starting location (address, city, or landmark)
- `radius` (required): Search radius in meters (0.0 to 50000.0)

**Response** (Success - 200 OK):
```json
{
  "destinationType": "cafe",
  "routeDetails": {
    "origin": "Charleston, SC",
    "destination": "Kudu Coffee & Craft Beer",
    "distance": "2.3 mi",
    "duration": "8 mins",
    "instructions": [
      "Head north on Meeting St toward George St.",
      "Turn left onto Spring St.",
      "Turn right onto King St.",
      "Destination will be on the left."
    ]
  }
}
```

**Response** (Error - 400 Bad Request):
```json
"Invalid origin location."
```

**Response** (Error - 404 Not Found):
```json
"No destination found for type cafe within the specified radius."
```

**Response** (Error - 500 Internal Server Error):
```json
"Error message describing what went wrong"
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/destination/generateDestination`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "origin": "Harbor Walk East - College of Charleston",
     "radius": 10000
   }
   ```
6. Click Send

**cURL Example**:
```bash
curl -X POST http://localhost:5001/destination/generateDestination \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "Charleston, SC",
    "radius": 5000
  }'
```

**How It Works**:
1. Converts your origin location to coordinates using Google Geocoding API
2. Randomly selects a destination type from:
   - `restaurant` - Dining establishments
   - `cafe` - Coffee shops and cafes
   - `bakery` - Bakeries and pastry shops
   - `amusement_center` - Entertainment venues
   - `cultural_center` - Cultural institutions
   - `movie_theater` - Cinema and theaters
   - `park` - Parks and outdoor spaces
   - `cultural_landmark` - Historical and cultural landmarks
3. Searches for a place of that type within your specified radius using Google Places API
4. Generates turn-by-turn driving directions from your origin to the destination
5. Returns both the destination type and complete route details

**Use Cases**:
- Discover new places when you're feeling adventurous
- Find something to do nearby without having to decide what
- Generate spontaneous date ideas or outing destinations
- Break out of routine by visiting random local spots

**Requirements**:
- The `GOOGLE_MAPS_API_KEY` must be configured in `application.yaml` or `application-test.yaml`
- Origin should be a valid location string that can be geocoded
- Radius must be between 0.0 and 50000.0 meters
- The endpoint uses Google Geocoding API, Google Places API (New), and Google Maps Directions API

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
- The `GOOGLE_MAPS_API_KEY` must be configured in `application.yaml` or `application-test.yaml`
- Valid latitude and longitude coordinates
- The endpoint uses Google Places API (New) with the `searchNearby` method
- Maximum of 20 results per type (combined results may contain up to 20 × number of types)

#### POST /nearest/amenity/current-geolocation
**Description**: Find amenities near your approximate location. Returns detailed information about places including ratings, contact information, accessibility options, and restaurant-specific details.

**URL**: `http://localhost:5001/nearest/amenity/current-geolocation`

**Method**: `POST`

**Headers**:
- `Content-Type`: `application/json`
  **Request Body**:
```json
{
  "radius": 1500,
  "type": "restaurant"
}
```

**Request Parameters**:
- `radius` (required): Search radius in meters (default: 1000, max: 50000)
- `type` (required): Type of amenity to search for (e.g., "restaurant", "gas_station", "park", "cafe")

**Response** (Success - 200 OK):
```json
{
"amenities": [
  {
    "placeId": "ChIJyz5Va1SeVogR8h36vS53-Po",
    "name": "Chick-fil-A",
    "vicinity": "1540 E Woodlawn Rd, Charlotte, NC 28209, USA",
    "rating": 4.5,
    "userRatingsTotal": 1779,
    "latitude": 35.171641199999996,
    "longitude": -80.8498977,
    "types": ["fast_food_restaurant", "breakfast_restaurant", "catering_service", "food_delivery", "restaurant", "food", "point_of_interest", "establishment"],
    "website": "https://www.chick-fil-a.com/locations/nc/east-woodlawn-road?utm_source=yext&utm_medium=link",
    "phoneNumber": "(704) 601-6031",
    "openNow": true,
    "wheelchairAccessibleEntrance": true,
    "priceLevelString": "PRICE_LEVEL_INEXPENSIVE",
    "hasRestroom": true,
    "dineIn": false,
    "takeout": true,
    "delivery": true,
    "servesBreakfast": true,
    "servesLunch": true,
    "servesDinner": true,
    "vegetarianFood": false,
    "music": false,
    "reservable": false
  }
],
  "error": null
}
```

**Response** (Error - 500 Internal Server Error):
```json
{
  "error": "Error fetching amenities: API request failed with response code: 400"
}
```

**Postman Setup**:
1. Create a new POST request
2. Enter URL: `http://localhost:5001/nearest/amenity/current-geolocation`
3. Select the **Body** tab
4. Choose **raw** and select **JSON** from the dropdown
5. Enter the JSON request body:
   ```json
   {
     "radius": 2000,
     "type": "cafe"
   }
   ```
6. Click Send

**cURL Example**:
```bash
curl -X POST http://localhost:5001/nearest/amenity/current-geolocation \
  -H "Content-Type: application/json" \
  -d '{
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

### Geolocation Endpoint

#### GET /current/geolocation/auto

**Description**:  Get your approximate current location in latitude and longitude.

**URL**: `http://localhost:5001/current/geolocation/auto`

**Method**: `GET`

**Response**:
```json
{
  "longitude": -80.8488498,
  "accessPointsUsed": [],
  "latitude": 35.1811188
}
```
**Postman Setup**:
1. Create a new GET request
2. Enter URL: `http://localhost:5001/current/geolocation/auto`
3. Click Send

### Health Check Endpoints

RouteScout provides multiple health check endpoints to monitor application status and performance.

#### GET /status/health

**Description**: Enhanced health check endpoint that provides detailed application health status along with system metrics including CPU usage, memory consumption, and HTTP request statistics.

**URL**: `http://localhost:5001/status/health`

**Method**: `GET`

**Response** (Success - 200 OK):
```json
{
  "health": {
    "status": "UP"
  },
  "metrics": {
    "system.cpu.usage": {
      "name": "system.cpu.usage",
      "description": "The recent CPU usage of the system",
      "baseUnit": "percent",
      "measurements": [
        {
          "statistic": "VALUE",
          "value": 0.125
        }
      ]
    },
    "process.cpu.usage": {
      "name": "process.cpu.usage",
      "description": "The recent CPU usage of the JVM process",
      "baseUnit": "percent",
      "measurements": [
        {
          "statistic": "VALUE",
          "value": 0.0523
        }
      ]
    },
    "jvm.memory.used": {
      "name": "jvm.memory.used",
      "description": "The amount of used memory",
      "baseUnit": "bytes",
      "measurements": [
        {
          "statistic": "VALUE",
          "value": 123456789
        }
      ]
    },
    "http.server.requests": {
      "name": "http.server.requests",
      "description": "HTTP server request metrics",
      "baseUnit": "seconds",
      "measurements": [
        {
          "statistic": "COUNT",
          "value": 42
        },
        {
          "statistic": "TOTAL_TIME",
          "value": 1.234
        }
      ]
    }
  }
}
```

**Postman Setup**:
1. Create a new GET request
2. Enter URL: `http://localhost:5001/status/health`
3. Click Send

**cURL Example**:
```bash
curl http://localhost:5001/status/health
```

**Use Cases**:
- Monitor application health and performance in real-time
- Track CPU and memory usage for capacity planning
- Monitor HTTP request metrics for load analysis
- Integration with monitoring tools and dashboards

#### GET /actuator/health

**Description**: Standard Spring Boot Actuator health check endpoint to verify the application is running.

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

**Note**: This is the standard Spring Boot Actuator health endpoint, enabled in `application.yaml` under the `management.endpoint.health.enabled` configuration. For detailed metrics, use the `/status/health` endpoint instead.

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

