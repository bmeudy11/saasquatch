package edu.citadel.api;

import edu.citadel.api.RouteEndpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.request.DestinationRequestBody;
import edu.citadel.api.request.RouteRequestBody;
import edu.citadel.api.response.RouteResponse;
import edu.citadel.dal.keys.APIKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/destination")
public class DestinationEndpoints {
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DestinationEndpoints.class);

    private final RouteEndpoints routeEndpoints;

    public DestinationEndpoints(APIKeys apiKeys, RouteEndpoints routeEndpoints) {
        this.apiKey = apiKeys.getMapsApiKey();
        this.routeEndpoints = routeEndpoints;
    }

    // Process the response from Geocode API (for getting lat/long)
    private double[] processGeoResponse(String response) throws Exception{
        JsonNode node = new ObjectMapper().readTree(response);

        if (!node.has("status") || !"OK".equals(node.get("status").asText())) {
            String status = node.has("status") ? node.get("status").asText() : "UNKNOWN";
            if ("ZERO_RESULTS".equals(status)) {
                return null;
            }
            throw new Exception("API Error: " + status);
        }

        if (node.has("results") && node.get("results").isArray()) {
            JsonNode results = node.get("results");

            if (!results.isEmpty()) {
                JsonNode firstResult = results.get(0);

                JsonNode location = firstResult.path("geometry").path("location");

                if (location.has("lat") && location.has("lng")) {
                    double lat = location.get("lat").asDouble();
                    double lng = location.get("lng").asDouble();

                    return new double[]{lat, lng};
                }
            }
        }
        throw new Exception("No coordinates found.");
    }

    // Build the request for Places API to get Place ID
    private Map<String, Object> buildPlacesRequest(double lat, double lng, double radius, String type) {
        Map<String, Object> requestBody = new HashMap<>();

        // Define the center coordinates of the search circle
        Map<String, Object> center = new HashMap<>();
        center.put("latitude", lat);
        center.put("longitude", lng);

        // Define the circle itself; requires a center and a radius
        Map<String, Object> circle = new HashMap<>();
        circle.put("center", center);
        circle.put("radius", radius);

        Map<String, Object> locationRestriction = new HashMap<>();
        locationRestriction.put("circle", circle);

        requestBody.put("locationRestriction", locationRestriction);

        requestBody.put("includedTypes", List.of(type.toLowerCase()));
        requestBody.put("maxResultCount", 1);

        return requestBody;
    }

    @PostMapping("/generateDestination")
    public ResponseEntity<?> generateDestination(@RequestBody DestinationRequestBody body) {
        String origin = body.getOrigin().replaceAll("\\s", ""); // Remove all spaces: needed for Geocoding to work
        try {
            // Get the latitude and longitude of the origin location from Google Geocoding API
            String GEOCODING_URI = "https://maps.googleapis.com/maps/api/geocode/json?address=" + origin + "&key=" + apiKey;
            URL url = new URL(GEOCODING_URI);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // If the latitude/longitude array is null, the origin is invalid.
            double[] processedResponse = processGeoResponse(response.toString());
            if (processedResponse == null) {
                return new ResponseEntity<>("Invalid origin location.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            double originLat = processedResponse[0];
            double originLong = processedResponse[1];

            connection.disconnect();
            connection = null;

            // Start generating the place to visit: randomly select a type of place from the categories below
            String[] types = {"restaurant", "cafe", "bakery", "amusement_center", "cultural_center", "movie_theater", "park", "cultural_landmark"};
            Random random = new Random();
            int typeChoiceInt = random.nextInt(types.length);
            String typeChoice = types[typeChoiceInt];

            // Create the request body for the Places API POST request
            Map<String, Object> requestBody = buildPlacesRequest(originLat, originLong, body.getRadius(), typeChoice);

            final String PLACES_URI = "https://places.googleapis.com/v1/places:searchNearby";
            URL placesUrl = new URL(PLACES_URI);

            HttpURLConnection conn = null;

            conn = (HttpURLConnection) placesUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Goog-Api-Key", apiKey);
            conn.setRequestProperty("X-Goog-FieldMask", "places.id,places.displayName");
            conn.setDoOutput(true);
            conn.connect();

            String jsonInputString = objectMapper.writeValueAsString(requestBody);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(jsonInputString.getBytes());
            }

            // If the API returns an error, process the response
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    throw new Exception(errorResponse.toString());
                }
            }

            // Process a successful response
            StringBuilder placesResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String placesLine;
                while ((placesLine = reader.readLine()) != null) {
                    placesResponse.append(placesLine);
                }
            }

            JsonNode placesNode = objectMapper.readTree(placesResponse.toString());
            JsonNode placesArray = placesNode.path("places");


            // Handle case where no places found
            if (placesArray.isEmpty()) {
                logger.warn("No destination found for type {} at location.", typeChoice);
                return new ResponseEntity<>(String.format("No destination found for type %s within the specified radius.", typeChoice), HttpStatus.NOT_FOUND);
            }

            // Save the Place ID (must be prefixed with "place_id:" for use in Directions API)
            JsonNode destination = placesArray.get(0);
            String placeId = "place_id:" + destination.get("id").asText();
            String placeName = destination.path("displayName").path("text").asText();


            RouteRequestBody routeRequestBody = new RouteRequestBody();
            routeRequestBody.setOrigin(body.getOrigin());
            routeRequestBody.setDestination(placeId);

            try {
                RouteResponse routeResponse = routeEndpoints.getDirections(routeRequestBody, placeName);

                Map <String, Object> combinedResponse = new HashMap<>();
                combinedResponse.put("destinationType", typeChoice);

                combinedResponse.put("routeDetails", routeResponse);
                return ResponseEntity.ok(combinedResponse);
            } catch (Exception e) {
                logger.error("Error generating route for random destination with getDirections: {}", e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("Error in generateDestination: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
