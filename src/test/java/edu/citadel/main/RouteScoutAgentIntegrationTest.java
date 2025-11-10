package edu.citadel.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RouteScoutAgent that use the REAL Gemini AI API.
 *
 * These tests require:
 * - GOOGLE_API_KEY environment variable to be set
 * - Active internet connection
 * - Gemini API quota available
 *
 * Run with: mvn test -Dgroups="integration"
 * Or run all tests: mvn verify
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
class RouteScoutAgentIntegrationTest {

    private RouteScoutAgent routeScoutAgent;

    @BeforeEach
    void setUp() {
        routeScoutAgent = new RouteScoutAgent();
    }

    @Test
    void testParsePOIQuery_WithRealAI_GasStation() throws Exception {
        // Arrange
        String query = "find me a gas station";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        // Real AI should return gas_station
        assertTrue(result.contains("gas_station"),
                   "AI should parse 'gas station' to 'gas_station', got: " + result);
    }

    @Test
    void testParsePOIQuery_WithRealAI_Restaurant() throws Exception {
        // Arrange
        String query = "fast food restaurants";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Should contain restaurant or meal_takeaway
        assertTrue(result.contains("restaurant") || result.contains("meal_takeaway"),
                   "AI should parse food-related query, got: " + result);
    }

    @Test
    void testParsePOIQuery_WithRealAI_Coffee() throws Exception {
        // Arrange
        String query = "coffee shop near me";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("cafe") || result.contains("coffee_shop"),
                   "AI should parse coffee query, got: " + result);
    }

    @Test
    void testParsePOIQuery_WithRealAI_MultipleTypes() throws Exception {
        // Arrange
        String query = "I need gas and food";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Should recognize both gas and food
        assertTrue(result.size() >= 2,
                   "AI should recognize multiple types, got: " + result);
        assertTrue(result.contains("gas_station") || result.contains("restaurant"),
                   "Should contain gas or food types, got: " + result);
    }

    @Test
    void testParsePOIQuery_WithRealAI_VagueQuery() throws Exception {
        // Arrange
        String query = "somewhere to stop on a road trip";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // AI should infer common road trip stops
        System.out.println("AI interpreted vague query as: " + result);
        assertTrue(result.size() >= 1, "AI should provide at least one suggestion");
    }

    @Test
    void testParsePOIQuery_WithRealAI_ComplexQuery() throws Exception {
        // Arrange
        String query = "I'm looking for a place to get coffee and use the restroom, " +
                      "maybe grab some snacks for the road";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Should recognize cafe/convenience store
        System.out.println("Complex query parsed to: " + result);
        assertTrue(result.size() >= 1, "Should recognize at least one POI type");
    }

    @Test
    void testParsePOIQuery_WithRealAI_SpecificBrand() throws Exception {
        // Arrange
        String query = "find me a Starbucks";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Should recognize Starbucks as cafe
        assertTrue(result.contains("cafe") || result.contains("coffee_shop"),
                   "AI should recognize Starbucks as cafe, got: " + result);
    }

    @Test
    void testParsePOIQuery_WithRealAI_ReturnValidJSON() throws Exception {
        // Arrange
        String query = "pharmacy";

        // Act
        List<String> result = routeScoutAgent.parsePOIQuery(query);

        // Assert - verify AI returns valid, parseable response
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // All results should be valid place type strings
        for (String placeType : result) {
            assertNotNull(placeType, "Place type should not be null");
            assertFalse(placeType.trim().isEmpty(), "Place type should not be empty");
            // Should be lowercase with underscores (Google Places format)
            assertTrue(placeType.matches("[a-z_]+"),
                      "Place type should be lowercase with underscores: " + placeType);
        }
    }

    @Test
    void testParsePOIQuery_WithRealAI_EdgeCase_Empty() throws Exception {
        // Arrange
        String query = "";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeScoutAgent.parsePOIQuery(query);
        });

        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @Test
    void testParsePOIQuery_WithRealAI_EdgeCase_Null() throws Exception {
        // Arrange
        String query = null;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeScoutAgent.parsePOIQuery(query);
        });

        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @Test
    void testParsePOIQuery_WithRealAI_ResponseTime() throws Exception {
        // Arrange
        String query = "gas station";

        // Act
        long startTime = System.currentTimeMillis();
        List<String> result = routeScoutAgent.parsePOIQuery(query);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        assertNotNull(result);
        System.out.println("AI response time: " + duration + "ms");

        // AI should respond in reasonable time (< 10 seconds)
        assertTrue(duration < 10000,
                  "AI should respond within 10 seconds, took: " + duration + "ms");
    }
}
