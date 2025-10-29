package edu.citadel.main;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class RouteScoutAgent {
    private Client genaiClient;
    private String modelName = "gemini-2.5-pro";

    public RouteScoutAgent(Client genaiClient) {
        this.genaiClient = new Client();
    }

    public String getSuggestions(String message) throws Exception {

        String prompt = String.format(
                "You are a helpful assistant for the RouteScout application.  You goal is to suggest \n" +
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

        System.out.println("Submitted message " + message + "\n");
        try {
            GenerateContentResponse response = genaiClient.models.generateContent(
                    modelName,
                    prompt,
                    null // Config is null, as JSON type is not supported in 1.0.0
            );
            System.out.println("Generated response: " + response);
            return response.text();

        } catch (Exception e) {
            System.out.println("Error in RouteScoutAgent " + e);
            throw new Exception("Failed to get suggestions.", e);
        }
    }
}
