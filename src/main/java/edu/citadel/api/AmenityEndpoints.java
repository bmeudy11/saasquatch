package edu.citadel.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.request.AmenityRequest;
import edu.citadel.api.request.AmenityRequestCurrent;
import edu.citadel.api.request.MultiTypeRequest;
import edu.citadel.dal.keys.APIKeys;
import edu.citadel.services.WifiScannerService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nearest")
public class AmenityEndpoints {
    private final String apiKey;
    private final ObjectMapper objectMapper;
    //private final APIKeys apiKeys;
    //private final WifiScannerService wifiScannerService;
    private static final String PLACES_API_URL = "https://places.googleapis.com/v1/places:searchNearby";
    private static final String FIELD_MASK =
            "places.id,places.displayName,places.formattedAddress,places.location," +
                    "places.rating,places.userRatingCount,places.types,places.websiteUri," +
                    "places.nationalPhoneNumber,places.currentOpeningHours,places.priceLevel," +
                    "places.accessibilityOptions,places.restroom," +
                    "places.servesVegetarianFood,places.delivery,places.takeout,places.dineIn," +
                    "places.servesBreakfast,places.servesLunch,places.servesDinner,places.reservable";
    private final GeolocationEndpoint geolocationEndpoint;

    public AmenityEndpoints(APIKeys apiKeys, GeolocationEndpoint geolocationEndpoint) {
        this.apiKey = apiKeys.getMapsApiKey();
        this.geolocationEndpoint = geolocationEndpoint;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Find amenities near a given location
     * POST /nearest/amenity
     * Body: {
     *   "latitude": 40.758896,
     *   "longitude": -73.985130,
     *   "radius": 1500,
     *   "type": "restaurant",
     *   "keyword": "pizza"
     * }
     */
    @PostMapping("/amenity")
    public ResponseEntity<?> nearestAmenity(@RequestBody AmenityRequest request) {
        try {
            Map<String, Object> requestBody = buildSearchRequest(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadius(),
                    request.getType() != null && !request.getType().isEmpty() ?
                            List.of(request.getType().toLowerCase()) : null
            );

            JsonNode response = makeSearchNearbyRequest(requestBody);
            List<AmenityDTO> amenities = convertJsonToAmenityList(response);

            return ResponseEntity.ok(new AmenityResponse(amenities, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching amenities: " + e.getMessage()));
        }

    }

    /**
     * Search amenities by specific types
     * POST /nearest/amenity/types
     */
    @PostMapping("/amenity/types")
    public ResponseEntity<?> amenitiesByTypes(@RequestBody MultiTypeRequest request) {
        try {
            List<AmenityDTO> allAmenities = new ArrayList<>();

            for (String typeStr : request.getTypes()) {
                Map<String, Object> requestBody = buildSearchRequest(
                        request.getLatitude(),
                        request.getLongitude(),
                        request.getRadius(),
                        List.of(typeStr.toLowerCase())
                );

                JsonNode response = makeSearchNearbyRequest(requestBody);
                allAmenities.addAll(convertJsonToAmenityList(response));
            }

            return ResponseEntity.ok(new AmenityResponse(allAmenities, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching amenities by types: " + e.getMessage()));
        }
    }

    @PostMapping("/amenity/current-geolocation")
    public ResponseEntity<?> currentGeolocation(@RequestBody AmenityRequestCurrent request) throws Exception {

        APIKeys apiKeys = new APIKeys();
        WifiScannerService wifiScannerService = new WifiScannerService();

        GeolocationEndpoint geo = new GeolocationEndpoint(apiKeys, wifiScannerService);
        ResponseEntity<?> geoResponse = this.geolocationEndpoint.autoGeolocation();

        // Check if geolocation was successful
        if (geoResponse.getStatusCode() != HttpStatus.OK) {
            // Extract the error message from the geolocation response
            Object body = geoResponse.getBody();
            String errorMsg = "Failed to get current location";

            if (body instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> errorData = (Map<String, Object>) body;
                if (errorData.containsKey("error")) {
                    errorMsg = "Geolocation error: " + errorData.get("error");
                }
            }

            System.err.println("Geolocation failed: " + errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", errorMsg));
        }

        // Extract latitude and longitude from the response body
        @SuppressWarnings("unchecked")
        Map<String, Object> geoData = (Map<String, Object>) geoResponse.getBody();

        if (geoData == null || !geoData.containsKey("latitude") || !geoData.containsKey("longitude")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Invalid geolocation response"));
        }

        double latitude = ((Number) geoData.get("latitude")).doubleValue();
        double longitude = ((Number) geoData.get("longitude")).doubleValue();

        System.out.println(latitude);
        System.out.println(longitude);
        try {
            Map<String, Object> requestBody = buildSearchRequest(
                    latitude,
                    longitude,
                    request.getRadius(),
                    request.getType() != null && !request.getType().isEmpty() ?
                            List.of(request.getType().toLowerCase()) : null
            );

            JsonNode response = makeSearchNearbyRequest(requestBody);
            List<AmenityDTO> amenities = convertJsonToAmenityList(response);

            return ResponseEntity.ok(new AmenityResponse(amenities, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching amenities: " + e.getMessage()));
        }
    }


    public Map<String, Object> buildSearchRequest(double latitude, double longitude, int radius, List<String> types) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> locationRestriction = new HashMap<>();
        Map<String, Object> circle = new HashMap<>();
        Map<String, Double> center = new HashMap<>();
        center.put("latitude", latitude);
        center.put("longitude", longitude);
        circle.put("center", center);
        circle.put("radius", radius != 0 ? (double) radius : 1000.0);
        locationRestriction.put("circle", circle);
        requestBody.put("locationRestriction", locationRestriction);

        if (types != null && !types.isEmpty()) {
            requestBody.put("includedTypes", types);
        }

        requestBody.put("maxResultCount", 20);

        return requestBody;
    }

    public JsonNode makeSearchNearbyRequest(Map<String, Object> requestBody) throws Exception {
        URL url = new URL(PLACES_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-Goog-Api-Key", apiKey);
        conn.setRequestProperty("X-Goog-FieldMask", FIELD_MASK);
        conn.setDoOutput(true);

        String jsonInputString = objectMapper.writeValueAsString(requestBody);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return readResponse(conn);
    }

    private JsonNode readResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return objectMapper.readTree(response.toString());
            }
        } else {
            throw new Exception("API request failed with response code: " + responseCode);
        }
    }

    public List<AmenityDTO> convertJsonToAmenityList(JsonNode responseNode) {
        List<AmenityDTO> amenities = new ArrayList<>();

        if (responseNode != null && responseNode.has("places")) {
            JsonNode places = responseNode.get("places");

            for (JsonNode place : places) {
                amenities.add(mapPlaceToDTO(place));
            }
        }

        return amenities;
    }

    private AmenityDTO mapPlaceToDTO(JsonNode place) {
        AmenityDTO dto = new AmenityDTO();

        // Basic info
        if (place.has("id")) {
            dto.setPlaceId(place.get("id").asText());
        }
        if (place.has("displayName") && place.get("displayName").has("text")) {
            dto.setName(place.get("displayName").get("text").asText());
        }
        if (place.has("formattedAddress")) {
            dto.setVicinity(place.get("formattedAddress").asText());
        }
        if (place.has("rating")) {
            dto.setRating((float) place.get("rating").asDouble());
        }
        if (place.has("userRatingCount")) {
            dto.setUserRatingsTotal(place.get("userRatingCount").asInt());
        }

        // Location
        if (place.has("location")) {
            JsonNode location = place.get("location");
            if (location.has("latitude")) {
                dto.setLatitude(location.get("latitude").asDouble());
            }
            if (location.has("longitude")) {
                dto.setLongitude(location.get("longitude").asDouble());
            }
        }

        // Types
        if (place.has("types")) {
            List<String> typesList = new ArrayList<>();
            for (JsonNode type : place.get("types")) {
                typesList.add(type.asText());
            }
            dto.setTypes(typesList.toArray(new String[0]));
        }

        // Contact info
        if (place.has("websiteUri")) {
            dto.setWebsite(place.get("websiteUri").asText());
        }
        if (place.has("nationalPhoneNumber")) {
            dto.setPhoneNumber(place.get("nationalPhoneNumber").asText());
        }

        // Opening hours
        if (place.has("currentOpeningHours")) {
            JsonNode openingHours = place.get("currentOpeningHours");
            if (openingHours.has("openNow")) {
                dto.setOpenNow(openingHours.get("openNow").asBoolean());
            }
        }

        // Accessibility
        if (place.has("accessibilityOptions")) {
            JsonNode accessibility = place.get("accessibilityOptions");
            if (accessibility.has("wheelchairAccessibleEntrance")) {
                dto.setWheelchairAccessibleEntrance(accessibility.get("wheelchairAccessibleEntrance").asBoolean());
            }
        }

        // Price level
        dto.setPriceLevelString(parsePriceLevel(place));

        // Restroom
        dto.setHasRestroom(place.has("restroom") && place.get("restroom").asBoolean());

        // Restaurant amenities
        if (isRestaurant(place)) {
            setRestaurantFields(dto, place);
        }

        // Music - would require review parsing
        dto.setMusic(false);

        return dto;
    }

    private String parsePriceLevel(JsonNode place) {
        if (!place.has("priceLevel")) {
            return "PRICE_LEVEL_UNSPECIFIED";
        }

        String priceLevel = place.get("priceLevel").asText();
        switch (priceLevel) {
            case "PRICE_LEVEL_FREE":
            case "PRICE_LEVEL_INEXPENSIVE":
            case "PRICE_LEVEL_MODERATE":
            case "PRICE_LEVEL_EXPENSIVE":
            case "PRICE_LEVEL_VERY_EXPENSIVE":
                return priceLevel;
            default:
                return "PRICE_LEVEL_UNSPECIFIED";
        }
    }

    private boolean isRestaurant(JsonNode place) {
        if (!place.has("types")) {
            return false;
        }

        for (JsonNode type : place.get("types")) {
            String typeStr = type.asText().toLowerCase();
            if (typeStr.equals("restaurant") || typeStr.equals("cafe") ||
                    typeStr.equals("bar") || typeStr.equals("food")) {
                return true;
            }
        }
        return false;
    }

    private void setRestaurantFields(AmenityDTO dto, JsonNode place) {
        if (place.has("servesVegetarianFood")) {
            dto.setVegetarianFood(place.get("servesVegetarianFood").asBoolean());
        }
        if (place.has("delivery")) {
            dto.setDelivery(place.get("delivery").asBoolean());
        }
        if (place.has("takeout")) {
            dto.setTakeout(place.get("takeout").asBoolean());
        }
        if (place.has("dineIn")) {
            dto.setDineIn(place.get("dineIn").asBoolean());
        }
        if (place.has("servesBreakfast")) {
            dto.setServesBreakfast(place.get("servesBreakfast").asBoolean());
        }
        if (place.has("servesLunch")) {
            dto.setServesLunch(place.get("servesLunch").asBoolean());
        }
        if (place.has("servesDinner")) {
            dto.setServesDinner(place.get("servesDinner").asBoolean());
        }
        if (place.has("reservable")) {
            dto.setReservable(place.get("reservable").asBoolean());
        }
    }
}

@Getter
@Setter
class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
