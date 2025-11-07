package edu.citadel.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.api.response.AISuggestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RouteScoutAgent {
    private static final Logger logger = LoggerFactory.getLogger(RouteScoutAgent.class);
    private final String modelName = "gemini-2.5-flash";
    //private final String modelName = "gemini-2.5-pro";

    private final Client genaiClient;

    public RouteScoutAgent() {
        this.genaiClient = new Client();
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
            return mapper.readValue(
                    Objects.requireNonNull(response.text())
                            .replaceAll("`", "")
                            .replaceAll("json", ""),
                    AISuggestionResponse.class);

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
            return mapper.readValue(
                    Objects.requireNonNull(response.text())
                            .replaceAll("`", "")
                            .replaceAll("json", ""),
                    AIPOIsResponse.class);

        } catch (Exception e) {
            logger.error("Error generating POIs for route from {} to {}", origin, destination, e);
            throw new Exception("Failed to get POIs.", e);
        }
    }
}
