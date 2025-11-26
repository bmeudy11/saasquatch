  
# SAASquatch UI

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Prerequisites

Before you begin, ensure you have the following installed:
- [Node.js](https://nodejs.org/) (version 14.0 or higher)
- npm (comes with Node.js)

## Installation

1. Navigate to the UI project directory:
```bash
cd ui/saasquatch-ui
```

2. Install all dependencies:
```bash
npm install
```

## Running the Application

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.\
You may also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

If you aren't satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you're on your own.

You don't have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn't feel obligated to use this feature. However we understand that this tool wouldn't be useful if you couldn't customize it when you are ready for it.

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── API/            # API integration layer
│   │   └── API.js      # Axios client and API functions
│   ├── Alerts/         # Alert notification system
│   ├── MapDisplay/     # Google Maps integration
│   │   └── MapDisplay.jsx
│   ├── RouteForm/      # Route form component
│   │   └── RouteForm.jsx
│   └── Sidebar/        # Navigation sidebar
├── pages/              # Page components (routes)
│   ├── Home/           # Home page
│   ├── RoutePlanner/   # Route planning/API testing page
│   │   ├── RoutePlannerPage.jsx   # Page component
│   │   └── RoutePlannerPage.scss  # Page styles
│   ├── NotFound/       # 404 page
│   └── NotAvailable/   # Mobile/tablet restriction page
├── hooks/              # Custom React hooks
│   ├── useApi.js       # Generic API call hook
│   ├── useRoutes.js    # Route generation hook
│   └── useAmenities.js # Amenity search hook
├── assets/             # Images, fonts, static files
└── styles/             # Global SCSS styles
```

## Google Maps Integration

The UI includes Google Maps visualization for displaying generated routes.

### Features

- **Route Visualization**: Displays driving routes as polylines on an interactive map
- **Markers**: Shows start (A) and end (B) markers for routes
- **Auto-centering**: Automatically centers and zooms the map to show the complete route
- **Polyline Decoding**: Decodes Google's encoded polyline format from backend responses

### Dependencies

- **@react-google-maps/api**: React wrapper for Google Maps JavaScript API
  - Installed automatically with `npm install`
  - Provides `GoogleMap`, `Polyline`, and `Marker` components
  - Handles Google Maps script loading and lifecycle

### Setup Requirements

1. **Google Maps API Key**: Must be configured in `.env` as `REACT_APP_GOOGLE_MAPS_API_KEY`
2. **Maps JavaScript API**: Must be enabled in Google Cloud Console for your API key
3. **Geometry Library**: Automatically loaded for polyline decoding

### Component

**MapDisplay** (`src/components/MapDisplay.jsx`)
- Accepts `routeData` prop containing encoded polyline
- Handles both direct route responses and wrapped destination responses
- Displays loading state while Google Maps initializes

## Backend Integration

### Connection Setup

- **Backend URL**: `http://localhost:5001` (default)
- **Frontend URL**: `http://localhost:3000`
- **CORS Configuration**: Backend uses `@CrossOrigin(origins = "http://localhost:3000")` annotations on all endpoint classes

### Running with Backend

1. Start the Spring Boot backend (runs on port 5001)
2. Start the React frontend with `npm start` (runs on port 3000)
3. Both services must be running for full functionality

## Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
REACT_APP_BASE_URL=http://localhost:5001
REACT_APP_GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
```

### Configuration Details

- **REACT_APP_BASE_URL**: Backend API URL (defaults to `http://localhost:5001` if not set)
- **REACT_APP_GOOGLE_MAPS_API_KEY**: Google Maps API key for map visualization
  - Required for displaying routes on Google Maps
  - Must have **Maps JavaScript API** enabled in Google Cloud Console
  - Should be the same API key configured in the backend (`application.yaml`)
  - Obtain from [Google Cloud Console](https://console.cloud.google.com/)

## API Integration Layer

Located in `src/components/API/API.js`, this file contains:

- **Axios instance** with base URL configuration
- **Request/response interceptors** for logging and error handling
- **API functions** that return `[success, data]` tuples

### Available API Functions

#### Health Endpoint

**`getServerHealth()`**
- Gets the current health status of the backend server
- No parameters required
- Returns: `[success, { health: { status: "UP" or "DOWN" } }]`

#### Route Endpoints

**`generateRoute(routeData)`**
- Generates a route between origin and destination
- Parameters:
  ```javascript
  {
    origin: "Charleston, SC",      // Starting location
    destination: "Atlanta, GA",     // Ending location
    waypoints: []                   // Optional waypoints array
  }
  ```
- Returns: `[success, routeData]` with distance, duration, and turn-by-turn instructions

**`generateRandomDestination(destinationData)`**
- Generates a random destination and route from an origin
- Parameters:
  ```javascript
  {
    origin: "Charleston, SC",       // Starting location
    radius: 50000                   // Search radius in feet
  }
  ```
- Returns: `[success, { destinationType, routeDetails }]`

#### Geolocation Endpoint

**`getCurrentLocation()`**
- Gets user's current location using WiFi-based geolocation
- No parameters required
- Returns: `[success, { latitude, longitude, accessPointsUsed }]`

#### Amenity Endpoints

**`findNearbyAmenity(amenityData)`**
- Finds a single type of amenity near a location
- Parameters:
  ```javascript
  {
    latitude: 34.0522,
    longitude: -118.2437,
    radius: 1500,                   // Search radius in meters
    type: "restaurant"              // Amenity type
  }
  ```

**`findAmenitiesByTypes(amenityData)`**
- Finds multiple types of amenities near a location
- Parameters:
  ```javascript
  {
    latitude: 34.0522,
    longitude: -118.2437,
    radius: 1500,                   // Search radius in meters
    types: ["restaurant", "gas_station"]  // Array of amenity types
  }
  ```

#### AI Endpoints

**`getAIPOIs(poiData)`**
- Gets AI-powered POI suggestions along a route
- Parameters:
  ```javascript
  {
    origin: "Charleston, SC",
    destination: "Atlanta, GA",
    query: "coffee shops"           // Optional natural language query
  }
  ```

### Using the API Functions

```javascript
import { generateRoute } from './components/API/API';

const handleGenerateRoute = async () => {
  const [success, data] = await generateRoute({
    origin: "Charleston, SC",
    destination: "Atlanta, GA",
    waypoints: []
  });

  if (success) {
    console.log("Route:", data);
  } else {
    console.error("Error:", data);
  }
};
```

## Custom Hooks

### `useApi(apiFunction)`

Generic hook for making API calls with loading and error states. Returns `{ data, loading, error, execute, reset }`.

### `useRoutes()`

Specialized hook for route generation. Returns `{ route, loading, error, createRoute, clearRoute }`.

### `useAmenities()`

Specialized hook for amenity searches. Returns `{ amenities, loading, error, searchAmenities, searchMultipleTypes, clearAmenities }`.

## Components

### RouteForm

Reusable form component for route planning input. Used in the RoutePlannerPage for API endpoint testing.

**Location**: `src/components/RouteForm/RouteForm.jsx`

**Props**:
- `onSubmit(formData)` - Callback function when form is submitted
- `loading` - Boolean to disable form during processing
- `alert` - Alert handler function for displaying notifications

**Features**:
- Origin selection: Manual address entry or WiFi-based geolocation
- Destination selection: Manual address entry or radius-based search
- Radius input for destination search (in feet, displays conversion to miles)
- Form validation with alert notifications
- Clear functionality

### RoutePlannerPage

API endpoint testing page for destination generation.

**Location**: `src/pages/RoutePlanner/RoutePlannerPage.jsx`

**Features**:
- Server health status indicator
- RouteForm integration for testing `/destination/generateDestination` endpoint
- Displays raw JSON response from API
- Real-time loading states

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: [https://facebook.github.io/create-react-app/docs/code-splitting](https://facebook.github.io/create-react-app/docs/code-splitting)

### Analyzing the Bundle Size

This section has moved here: [https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size](https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size)

### Making a Progressive Web App

This section has moved here: [https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app](https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app)

### Advanced Configuration

This section has moved here: [https://facebook.github.io/create-react-app/docs/advanced-configuration](https://facebook.github.io/create-react-app/docs/advanced-configuration)

### Deployment

This section has moved here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)

### `npm run build` fails to minify

This section has moved here: [https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify](https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify)
