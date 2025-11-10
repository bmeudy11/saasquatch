package edu.citadel.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import edu.citadel.dal.keys.APIKeys;
import edu.citadel.dal.model.Suggestion;
import edu.citadel.main.Prompts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for searching Points of Interest (POIs) along a travel route.
 * Uses Google Maps Directions API to get route information and Google Gemini AI
 * to generate contextual POI suggestions.
 */
@Service
public class POISearchService {
    private static final Logger logger = LoggerFactory.getLogger(POISearchService.class);
    private static final String MODEL_NAME = "gemini-2.5-flash";

    private final GeoApiContext geoApiContext;
    private final Client genaiClient;

    /**
     * Constructor for POISearchService.
     * Initializes Google Maps API context and Gemini AI client.
     *
     * @param apiKeys APIKeys service containing API credentials
     */
    public POISearchService(APIKeys apiKeys) {
        this.geoApiContext = new GeoApiContext.Builder()
                .apiKey(apiKeys.getMapsApiKey())
                .build();
        this.genaiClient = new Client();
        logger.info("POISearchService initialized with API keys");
    }

    /**
     * Searches for Points of Interest along a route between origin and destination.
     * Uses AI to generate contextual, relevant POI suggestions based on the route
     * and requested place types.
     *
     * @param origin Starting location (e.g., "Charleston, SC")
     * @param destination Ending location (e.g., "Columbia, SC")
     * @param placeTypes List of Google Places API place types (e.g., ["gas_station", "restaurant"])
     * @return List of Suggestion objects containing POI details
     * @throws Exception if the route cannot be found or if there's an error processing the request
     */
    public List<Suggestion> searchPOIsAlongRoute(String origin, String destination, List<String> placeTypes) throws Exception {
        // Validate inputs
        if (origin == null || origin.trim().isEmpty()) {
            logger.error("Origin is null or empty");
            throw new IllegalArgumentException("Origin cannot be null or empty");
        }
        if (destination == null || destination.trim().isEmpty()) {
            logger.error("Destination is null or empty");
            throw new IllegalArgumentException("Destination cannot be null or empty");
        }
        if (placeTypes == null) {
            logger.error("PlaceTypes list is null");
            throw new IllegalArgumentException("PlaceTypes cannot be null");
        }

        logger.info("Searching for POIs along route from {} to {} for place types: {}",
                    origin, destination, placeTypes);

        try {
            // Get route information from Google Maps Directions API
            DirectionsResult directionsResult;
            try {
                directionsResult = DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.DRIVING)
                        .origin(origin)
                        .destination(destination)
                        .await();
            } catch (Exception e) {
                // Check if it's a location error
                String errorMsg = e.getMessage();
                if (errorMsg != null && (errorMsg.contains("ZERO_RESULTS") || errorMsg.contains("NOT_FOUND"))) {
                    logger.error("No route found - invalid location: {}", errorMsg);
                    throw new Exception("No route found - ZERO_RESULTS: " + errorMsg, e);
                }
                throw e;
            }

            if (directionsResult == null || directionsResult.routes == null || directionsResult.routes.length == 0) {
                logger.error("No route found between {} and {}", origin, destination);
                throw new Exception("No route found between the specified locations");
            }

            // Extract route details
            String distance = directionsResult.routes[0].legs[0].distance.humanReadable;
            String duration = directionsResult.routes[0].legs[0].duration.humanReadable;

            logger.debug("Route found: {} distance, {} duration", distance, duration);

            // Try to generate AI-powered POI suggestions
            try {
                List<Suggestion> aiSuggestions = generateAIPOISuggestions(
                        origin, destination, distance, duration, placeTypes);

                if (aiSuggestions != null && !aiSuggestions.isEmpty()) {
                    logger.info("Successfully generated {} AI-powered POI suggestions", aiSuggestions.size());
                    return aiSuggestions;
                }
            } catch (Exception e) {
                logger.warn("AI POI generation failed, falling back to basic suggestions: {}", e.getMessage());
            }

            // Fallback to basic suggestions if AI fails
            logger.info("Using fallback suggestion generation");
            return generateFallbackSuggestions(origin, destination, distance, duration, placeTypes);

        } catch (Exception e) {
            logger.error("Error searching for POIs along route: {}", e.getMessage(), e);
            // Preserve important error indicators in the message
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("No route found") || errorMsg.contains("ZERO_RESULTS"))) {
                // Re-throw with original message to preserve error indicators
                throw e;
            }
            throw new Exception("Failed to search for POIs: " + e.getMessage(), e);
        }
    }

    /**
     * Generates POI suggestions using Google Gemini AI.
     * Creates contextual, intelligent suggestions based on the route and user preferences.
     *
     * @param origin Starting location
     * @param destination Ending location
     * @param distance Route distance in human-readable format
     * @param duration Route duration in human-readable format
     * @param placeTypes Requested place types
     * @return List of AI-generated Suggestion objects
     * @throws Exception if AI generation fails
     */
    private List<Suggestion> generateAIPOISuggestions(String origin, String destination,
                                                      String distance, String duration,
                                                      List<String> placeTypes) throws Exception {
        // Format place types for display
        String formattedTypes = placeTypes.stream()
                .map(this::formatPlaceType)
                .collect(Collectors.joining(", "));

        // Create the AI prompt
        String prompt = String.format(Prompts.AI_POI_SUGGESTIONS_PROMPT,
                origin, destination, distance, duration, formattedTypes);

        logger.debug("Sending AI request for POI suggestions");

        try {
            // Call Gemini AI
            GenerateContentResponse response = genaiClient.models.generateContent(
                    MODEL_NAME,
                    prompt,
                    null
            );

            String responseText = Objects.requireNonNull(response.text())
                    .replaceAll("`", "")
                    .replaceAll("json", "");

            logger.debug("Received AI response: {}", responseText);

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseText);
            JsonNode suggestionsNode = rootNode.get("suggestions");

            if (suggestionsNode == null || !suggestionsNode.isArray()) {
                throw new Exception("Invalid AI response format: missing suggestions array");
            }

            List<Suggestion> suggestions = new ArrayList<>();
            for (JsonNode suggestionNode : suggestionsNode) {
                Suggestion suggestion = new Suggestion();
                suggestion.setName(suggestionNode.get("name").asText());
                suggestion.setType(suggestionNode.get("type").asText());
                suggestion.setAddress(suggestionNode.get("address").asText());
                suggestion.setReason(suggestionNode.get("reason").asText());
                suggestions.add(suggestion);
            }

            return suggestions;

        } catch (Exception e) {
            logger.error("Error generating AI POI suggestions: {}", e.getMessage(), e);
            throw new Exception("AI POI generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Generates fallback POI suggestions when AI is unavailable.
     * Creates basic, generic suggestions based on the requested place types.
     *
     * @param origin Starting location
     * @param destination Ending location
     * @param distance Route distance in human-readable format
     * @param duration Route duration in human-readable format
     * @param placeTypes Requested place types
     * @return List of basic Suggestion objects
     */
    private List<Suggestion> generateFallbackSuggestions(String origin, String destination,
                                                         String distance, String duration,
                                                         List<String> placeTypes) {
        List<Suggestion> suggestions = new ArrayList<>();

        logger.info("Generating fallback suggestions for {} place types", placeTypes.size());

        // Generate a basic suggestion for each requested place type
        for (String placeType : placeTypes) {
            if (placeType == null || placeType.trim().isEmpty()) {
                continue;
            }

            Suggestion suggestion = new Suggestion();
            String formattedType = formatPlaceType(placeType);

            suggestion.setName("Recommended " + formattedType);
            suggestion.setType(formattedType);
            suggestion.setAddress("Along route from " + origin + " to " + destination);
            suggestion.setReason(String.format(
                    "A convenient %s stop along your %s route (approximately %s drive)",
                    formattedType.toLowerCase(), distance, duration));

            suggestions.add(suggestion);
        }

        logger.info("Generated {} fallback suggestions", suggestions.size());
        return suggestions;
    }

    /**
     * Formats a Google Places API place type string into a human-readable display name.
     * Converts underscores to spaces and capitalizes words.
     *
     * @param placeType The place type in Google Places format (e.g., "gas_station")
     * @return Formatted type name (e.g., "Gas Station")
     */
    private String formatPlaceType(String placeType) {
        if (placeType == null || placeType.trim().isEmpty()) {
            return "Location";
        }

        // Convert underscores to spaces and capitalize each word
        String[] words = placeType.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    formatted.append(word.substring(1).toLowerCase());
                }
                formatted.append(" ");
            }
        }

        return formatted.toString().trim();
    }
}
