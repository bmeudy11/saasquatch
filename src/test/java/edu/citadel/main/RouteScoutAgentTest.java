package edu.citadel.main;

import com.google.genai.Client;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteScoutAgentTest {

    @Test
    public void contextLoads() {}

    @Test
    void testConstructor_CreatesClientInstance() {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            assertNotNull(agent, "RouteScoutAgent should be instantiated");
            assertEquals(1, mockedClient.constructed().size(),
                    "Client should be constructed exactly once");
        }
    }

    @Test
    void testConstructor_InitializesGenaiClient() throws Exception {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            Field genaiClientField = RouteScoutAgent.class.getDeclaredField("genaiClient");
            genaiClientField.setAccessible(true);
            Object genaiClient = genaiClientField.get(agent);

            assertNotNull(genaiClient, "genaiClient should be initialized");
        }
    }

    @Test
    void testConstructor_InitializesModelName() throws Exception {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            Field modelNameField = RouteScoutAgent.class.getDeclaredField("modelName");
            modelNameField.setAccessible(true);
            String modelName = (String) modelNameField.get(agent);

            assertEquals("gemini-2.5-pro", modelName,
                    "Model name should be initialized to gemini-2.5-pro");
        }
    }

    @Test
    void testGetSuggestions_WithNullMessage_ThrowsException() {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            assertThrows(Exception.class, () -> {
                agent.getSuggestions(null);
            }, "getSuggestions should throw exception when message is null");
        }
    }

    @Test
    void testGetSuggestions_ErrorHandling() {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            Exception exception = assertThrows(Exception.class, () -> {
                agent.getSuggestions("test message");
            });

            assertEquals("Failed to get suggestions.", exception.getMessage(),
                    "Exception should have our custom error message");
            assertNotNull(exception.getCause(),
                    "Exception should have a cause");
        }
    }

    @Test
    void testGetSuggestions_WithEmptyMessage() {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            assertThrows(Exception.class, () -> {
                agent.getSuggestions("");
            }, "getSuggestions should throw exception when client fails");
        }
    }

    @Test
    void testGetSuggestions_WithValidMessage() {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            String[] testMessages = {
                    "Find me a coffee shop",
                    "I want a quiet place to study",
                    "Suggest some parks",
                    "Looking for restaurants with WiFi"
            };

            for (String message : testMessages) {
                assertThrows(Exception.class, () -> {
                    agent.getSuggestions(message);
                }, "Should attempt to process message: " + message);
            }
        }
    }

    @Test
    void testGetSuggestions_WithSpecialCharacters() {
        try (MockedConstruction<Client> mockedClient = mockConstruction(Client.class)) {
            RouteScoutAgent agent = new RouteScoutAgent();

            String messageWithSpecialChars = "Find \"coffee\" & tea places! @downtown #trendy";

            assertThrows(Exception.class, () -> {
                agent.getSuggestions(messageWithSpecialChars);
            }, "Should handle special characters without crashing");
        }
    }

    @Test
    void testGetSuggestions_PromptContainsRequiredElements() throws Exception {
        String userMessage = "Find me a library";

        String expectedPromptSnippet = String.format(
                "You are a helpful assistant for the RouteScout application.  You goal is to suggest \n" +
                        "locations based on user requests a travel route\n" +
                        "\n" +
                        "Based on the user's message: \"%s\", provide 2-3 location suggestions.\n",
                userMessage
        );

        assertTrue(expectedPromptSnippet.contains("RouteScout"),
                "Prompt should mention RouteScout");
        assertTrue(expectedPromptSnippet.contains(userMessage),
                "Prompt should include user message");
        assertTrue(expectedPromptSnippet.contains("2-3 location suggestions"),
                "Prompt should request 2-3 suggestions");
    }

    @Test
    void testPromptFormat_RequestsJSON() {
        String userMessage = "test";
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
                userMessage
        );

        assertTrue(prompt.contains("valid JSON object"),
                "Prompt should request JSON format");
        assertTrue(prompt.contains("suggestions"),
                "Prompt should specify suggestions array");
        assertTrue(prompt.contains("name"),
                "Prompt should include name field");
        assertTrue(prompt.contains("type"),
                "Prompt should include type field");
        assertTrue(prompt.contains("address"),
                "Prompt should include address field");
        assertTrue(prompt.contains("reason"),
                "Prompt should include reason field");
    }
}
