package edu.citadel.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.api.response.AISuggestionResponse;
import edu.citadel.dal.keys.APIKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RouteScoutAgent {
    private static final Logger logger = LoggerFactory.getLogger(RouteScoutAgent.class);
    private final String modelName = "gemini-2.5-flash";
    //private final String modelName = "gemini-2.5-pro";

    private final Client genaiClient;

    public RouteScoutAgent(APIKeys apiKeys) {
        this.genaiClient = Client.builder().apiKey(apiKeys.getGeminiApiKey()).build();
    }

    public AISuggestionResponse getSuggestions(String message) throws Exception {
        if (message == null || message.trim().isEmpty()) {
            logger.warn("Received null or empty message for suggestion request");
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        String prompt = String.format(Prompts.SUGGESTION_PROMPT, message);

        logger.info("Processing suggestion request for message: {}", message);
        try {
            GenerateContentResponse response = genaiClient.models.generateContent(
                    modelName,
                    prompt,
                    null
            );
            logger.debug("getSuggestions generated response: {}", response);
            ObjectMapper mapper = new ObjectMapper();
            String cleanedResponse = Objects.requireNonNull(response.text()).replaceAll("`", "").replaceAll("json", "");
            return mapper.readValue(cleanedResponse, AISuggestionResponse.class);

        } catch (Exception e) {
            logger.error("Error generating suggestions for message: {}", message, e);
            throw new Exception("Failed to get suggestions.", e);
        }
    }

    public AIPOIsResponse getAIPOIs(String origin, String destination) throws Exception {
        if ((origin == null || origin.trim().isEmpty()) | (destination == null || destination.trim().isEmpty())) {
            logger.warn("Received null or empty origin/destination for POI request");
            throw new IllegalArgumentException("Origin and destination cannot be null or empty");
        }

        String prompt = String.format(Prompts.POI_ROUTE_PROMPT, origin, destination);

        logger.info("Processing POI request for route from {} to {}", origin, destination);
        try {
            GenerateContentResponse response = genaiClient.models.generateContent(
                    modelName,
                    prompt,
                    null
            );
            logger.debug("getAIPOIs generated response: {}", response);
            ObjectMapper mapper = new ObjectMapper();
            String cleanedResponse = Objects.requireNonNull(response.text()).replaceAll("`", "").replaceAll("json", "");
            return mapper.readValue(cleanedResponse, AIPOIsResponse.class);

        } catch (Exception e) {
            logger.error("Error generating POIs for route from {} to {}", origin, destination, e);
            throw new Exception("Failed to get POIs.", e);
        }
    }

    /**
     * Parses a natural language POI query and converts it to Google Places API place types.
     * Uses Gemini AI to intelligently interpret user intent and map it to valid place type identifiers.
     *
     * @param query Natural language query (e.g., "find me a gas station", "coffee shop")
     * @return List of Google Places API place type identifiers (e.g., ["gas_station", "cafe"])
     * @throws Exception if the query cannot be parsed or if there's an AI error
     */
    public List<String> parsePOIQuery(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Received null or empty query for POI parsing");
            throw new IllegalArgumentException("Query cannot be null or empty");
        }

        String prompt = String.format(Prompts.PARSE_POI_QUERY_PROMPT, query);

        logger.info("Parsing POI query: {}", query);
        try {
            GenerateContentResponse response = genaiClient.models.generateContent(
                    modelName,
                    prompt,
                    null
            );
            logger.debug("parsePOIQuery generated response: {}", response);

            String responseText = Objects.requireNonNull(response.text()).replaceAll("`", "").replaceAll("json", "");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseText);
            JsonNode placeTypesNode = rootNode.get("placeTypes");

            if (placeTypesNode == null || !placeTypesNode.isArray()) {
                throw new Exception("Invalid AI response format: missing placeTypes array");
            }

            List<String> placeTypes = new ArrayList<>();
            for (JsonNode typeNode : placeTypesNode) {
                placeTypes.add(typeNode.asText());
            }

            if (placeTypes.isEmpty()) {
                throw new Exception("AI returned empty place types list");
            }

            logger.info("Parsed query '{}' to place types: {}", query, placeTypes);
            return placeTypes;

        } catch (Exception e) {
            logger.error("Error parsing POI query '{}': {}", query, e.getMessage(), e);
            throw new Exception("Failed to parse POI query.", e);
        }
    }
}
