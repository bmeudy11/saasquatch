import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

// Create configured axios instance
const api = axios.create({
    baseURL: BASE_URL || 'http://localhost:5001',
    timeout: 30000, // 30 second timeout
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor
api.interceptors.request.use(
    (config) => {
        // Log requests in development
        if (process.env.NODE_ENV === 'development') {
            console.log(`[API] ${config.method.toUpperCase()} ${config.url}`);
        }
        return config;
    },
    (error) => {
        console.error('[API Request Error]', error);
        return Promise.reject(error);
    }
);

// Response interceptor
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('[API Error]', error.response?.data || error.message);
        return Promise.reject(error);
    }
);

// Helper for request handling, DRY
const handleRequest = async (requestFn) => {
    try {
        const response = await requestFn();
        return [true, response.data];
    } catch (e) {
        console.error(e);
        return [false, e.response?.data?.error || e.message];
    }
};

/** HEALTH ENDPOINT
 * Get the current health status of the server
 * @returns {Promise<[boolean, object]>} [success, {data - see swagger for response object}]
 */
export const getServerHealth = async () => {
    return handleRequest(() => api.get('/status/health'));
};

/** ROUTE ENDPOINTS
 * Generate a route with Google Maps
 * @param {Object} routeData
 * @param {string} routeData.origin - Starting location (e.g., "Charleston, SC")
 * @param {string} routeData.destination - Ending location
 * @param {string[]} routeData.waypoints - Optional waypoints along the route
 * @returns {Promise<[boolean, object]>} [success, data]
 */
export const generateRoute = async (routeData) => {
    return handleRequest(() =>
        api.post('/route/generateRoute', {
            origin: routeData.origin,
            destination: routeData.destination,
            waypoints: routeData.waypoints || []
        })
    );
};

/**
 * Generate a random destination and route from an origin
 * @param {Object} destinationData
 * @param {string} destinationData.origin - Starting location (e.g., "Charleston, SC")
 * @param {number} destinationData.radius - Radius in feet to search for random destination
 * @returns {Promise<[boolean, object]>} [success, data with destination and route info]
 */
export const generateRandomDestination = async (destinationData) => {
    return handleRequest(() =>
        api.post('/destination/generateDestination', {
            origin: destinationData.origin,
            radius: destinationData.radius
        })
    );
};

/** AMENITY ENDPOINTS
 * Find amenities near a specific location
 * @param {Object} amenityData
 * @param {number} amenityData.latitude
 * @param {number} amenityData.longitude
 * @param {number} amenityData.radius - Search radius in meters (default 1500)
 * @param {string} amenityData.type - Type of amenity (e.g., "restaurant", "gas_station")
 * @returns {Promise<[boolean, object]>} [success, data]
 */
export const findNearbyAmenity = async (amenityData) => {
    return handleRequest(() =>
        api.post('/nearest/amenity', {
            latitude: amenityData.latitude,
            longitude: amenityData.longitude,
            radius: amenityData.radius || 1500,
            type: amenityData.type
        })
    );
};

/** Find multiple types of amenities near a location
 * @param {Object} amenityData
 * @param {number} amenityData.latitude
 * @param {number} amenityData.longitude
 * @param {number} amenityData.radius - Search radius in meters
 * @param {string[]} amenityData.types - Array of amenity types
 * @returns {Promise<[boolean, object]>} [success, data]
 */
export const findAmenitiesByTypes = async (amenityData) => {
    return handleRequest(() =>
        api.post('/nearest/amenity/types', {
            latitude: amenityData.latitude,
            longitude: amenityData.longitude,
            radius: amenityData.radius || 1500,
            types: amenityData.types
        })
    );
};


/** GEOLOCATION ENDPOINT
 * Get current location using WiFi-based geolocation
 * @returns {Promise<[boolean, object]>} [success, {latitude, longitude, accessPointsUsed}]
 */
export const getCurrentLocation = async () => {
    return handleRequest(() => api.get('/current/geolocation/auto'));
};

/** AI ENDPOINTS (Google Gemini)
 * Health check for AI service
 * @returns {Promise<[boolean, object]>} [success, data]
 */
export const checkAIServiceHealth = async () => {
    return handleRequest(() => api.get('/suggest'));
};
// I am not implementing the suggest endpoint because we want to have our requests specifically
// suggested along the route which is what the suggestPOIs endpoint does.

/**
 * Get AI-powered POI suggestions along a route
 * @param {Object} poiData
 * @param {string} poiData.origin - Starting location
 * @param {string} poiData.destination - Ending location
 * @param {string} poiData.query - Optional natural language query (e.g., "coffee shops")
 * @returns {Promise<[boolean, object]>} [success, data]
 */
export const getAIPOIs = async (poiData) => {
    return handleRequest(() =>
        api.post('/suggest/POIs', {
            origin: poiData.origin,
            destination: poiData.destination,
            query: poiData.query || ''
        })
    );
};
