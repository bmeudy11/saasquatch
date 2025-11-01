package edu.citadel.main;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RouteScoutAgent {
    private static final Logger logger = LoggerFactory.getLogger(RouteScoutAgent.class);

    private Client genaiClient;
    private String modelName = "gemini-2.5-pro";

    public RouteScoutAgent() {
        this.genaiClient = new Client();
    }

    public String getSuggestions(String message) throws Exception {
        if (message == null || message.trim().isEmpty()) {
            logger.warn("Received null or empty message for suggestion request");
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        String prompt = String.format(
                "You are a helpful assistant for the RouteScout application. Your goal is to suggest \n" +
                        "locations based on user requests a travel route\n" +
                        "\n" +
                        "Based on the user's message: \"%s\", provide 2-3 location suggestions.\n" +
                        "\n" +
                        "VERY IMPORTANT:  Respond ONLY with valid JSON object.  Do not include any text before or after the JSON.\n" +
                        "\n" +
                        "The JSON object should follow this structure, with no prefixes:\n" +
                        "{\n" +
                        "    \"suggestions\" : [\n" +
                        "        {\n" +
                        "            \"name\" : \"Location Name\",\n" +
                        "            \"type\" : \"ex, Cafe, Park, Library\",\n" +
                        "            \"address\" : \"The address of the location.\",\n" +
                        "            \"reason\" : \"A brief explanation of why this location fits the user's request.\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}",
                message
        );

        logger.info("Processing suggestion request for message: {}", message);
        try {
            GenerateContentResponse response = genaiClient.models.generateContent(
                    modelName,
                    prompt,
                    null // Config is null, as JSON type is not supported in 1.0.0
            );
            logger.debug("Generated response: {}", response);
            return response.text();

        } catch (Exception e) {
            logger.error("Error generating suggestions for message: {}", message, e);
            throw new Exception("Failed to get suggestions.", e);
        }
    }
}
