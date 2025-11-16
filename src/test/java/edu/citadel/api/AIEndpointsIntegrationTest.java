package edu.citadel.api;

import edu.citadel.api.request.AIPOIsRequestBody;
import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.dal.keys.APIKeys;
import edu.citadel.dal.model.Suggestion;
import edu.citadel.main.RouteScoutAgent;
import edu.citadel.services.POISearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * End-to-end integration tests for the POI suggestion feature.
 * Tests the complete flow: API endpoint → AI parsing → Route search → POI generation
 *
 * These tests require:
 * - GOOGLE_MAPS_API_KEY environment variable
 * - GOOGLE_API_KEY environment variable
 * - Active internet connection
 * - API quota available
 *
 * Run with: mvn test -Dgroups="integration"
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "GOOGLE_MAPS_API_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
class AIEndpointsIntegrationTest {

    @Mock
    private APIKeys apiKeys;

    private AIEndpoints aiEndpoints;
    private RouteScoutAgent routeScoutAgent;
    private POISearchService poiSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Use real API keys from environment
        String mapsApiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        String geminiApiKey = System.getenv("GOOGLE_API_KEY");
        when(apiKeys.getMapsApiKey()).thenReturn(mapsApiKey);
        when(apiKeys.getGeminiApiKey()).thenReturn(geminiApiKey);

        // Create real instances (not mocked)
        routeScoutAgent = new RouteScoutAgent(apiKeys);
        poiSearchService = new POISearchService(apiKeys);
        aiEndpoints = new AIEndpoints(routeScoutAgent, poiSearchService);
    }

    @Test
    void testGetPOIs_EndToEnd_GasStation() throws Exception {
        // Arrange
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("Charleston, SC");
        request.setDestination("Columbia, SC");
        request.setQuery("find me a gas station");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AIPOIsResponse);

        AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();
        assertNotNull(poiResponse.getSuggestions());
        assertFalse(poiResponse.getSuggestions().isEmpty(),
                   "Should return at least one gas station suggestion");

        // Verify first suggestion
        Suggestion first = poiResponse.getSuggestions().get(0);
        assertNotNull(first.getName());
        assertNotNull(first.getType());
        assertNotNull(first.getAddress());
        assertNotNull(first.getReason());

        System.out.println("=== End-to-End Gas Station Test ===");
        System.out.println("Query: " + request.getQuery());
        System.out.println("Route: " + request.getOrigin() + " → " + request.getDestination());
        System.out.println("Results (" + poiResponse.getSuggestions().size() + "):");
        poiResponse.getSuggestions().forEach(poi ->
            System.out.println("  - " + poi.getName() + " | " + poi.getAddress() + " | " + poi.getReason())
        );
    }

    @Test
    void testGetPOIs_EndToEnd_Restaurant() throws Exception {
        // Arrange
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("Charleston, SC");
        request.setDestination("Savannah, GA");
        request.setQuery("fast food restaurants");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();

        assertNotNull(poiResponse);
        assertFalse(poiResponse.getSuggestions().isEmpty());

        System.out.println("\n=== End-to-End Restaurant Test ===");
        System.out.println("Found " + poiResponse.getSuggestions().size() + " restaurants");
        poiResponse.getSuggestions().forEach(poi ->
            System.out.println("  - " + poi.getName())
        );
    }

    @Test
    void testGetPOIs_EndToEnd_CoffeeShop() throws Exception {
        // Arrange
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("New York, NY");
        request.setDestination("Philadelphia, PA");
        request.setQuery("coffee shop");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();

        assertNotNull(poiResponse);
        assertFalse(poiResponse.getSuggestions().isEmpty());

        System.out.println("\n=== End-to-End Coffee Shop Test ===");
        System.out.println("Route: NYC → Philly");
        System.out.println("Found " + poiResponse.getSuggestions().size() + " coffee shops");
    }

    @Test
    void testGetPOIs_EndToEnd_NaturalLanguageQuery() throws Exception {
        // Arrange - Test with very natural language
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("Charleston, SC");
        request.setDestination("Columbia, SC");
        request.setQuery("I need to stop for gas and grab some food");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();

        assertNotNull(poiResponse);
        assertFalse(poiResponse.getSuggestions().isEmpty());

        System.out.println("\n=== Natural Language Query Test ===");
        System.out.println("Query: \"" + request.getQuery() + "\"");
        System.out.println("AI understood and found " + poiResponse.getSuggestions().size() + " POIs");
        poiResponse.getSuggestions().forEach(poi ->
            System.out.println("  - " + poi.getName() + " (" + poi.getType() + ")")
        );
    }

    @Test
    void testGetPOIs_EndToEnd_LongRoute() throws Exception {
        // Arrange
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("Charleston, SC");
        request.setDestination("Atlanta, GA");
        request.setQuery("gas stations");

        // Act
        long startTime = System.currentTimeMillis();
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);
        long duration = System.currentTimeMillis() - startTime;

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();

        assertNotNull(poiResponse);
        assertFalse(poiResponse.getSuggestions().isEmpty());

        System.out.println("\n=== Long Route Test ===");
        System.out.println("Charleston → Atlanta");
        System.out.println("Response time: " + duration + "ms");
        System.out.println("Found " + poiResponse.getSuggestions().size() + " POIs");

        // Should respond in reasonable time
        assertTrue(duration < 20000, "Should complete within 20 seconds");
    }

    @Test
    void testGetPOIs_EndToEnd_ValidationStillWorks() {
        // Arrange - Test that validation still catches bad input
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin(null);
        request.setDestination("Columbia, SC");
        request.setQuery("gas station");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert - Should still validate properly
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Origin and Destination are required"));

        System.out.println("\n=== Validation Test ===");
        System.out.println("Null origin correctly rejected");
    }

    @Test
    void testGetPOIs_EndToEnd_InvalidLocation() {
        // Arrange
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("InvalidCityXYZ123");
        request.setDestination("Columbia, SC");
        request.setQuery("gas station");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert - Should return 500 error for invalid location
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        System.out.println("\n=== Invalid Location Test ===");
        System.out.println("Invalid location correctly returned 500 error");
    }

    @Test
    void testGetPOIs_EndToEnd_VerifyCompleteResponse() throws Exception {
        // Arrange
        AIPOIsRequestBody request = new AIPOIsRequestBody();
        request.setOrigin("Charleston, SC");
        request.setDestination("Columbia, SC");
        request.setQuery("convenience stores");

        // Act
        ResponseEntity<?> response = aiEndpoints.getPOIs(request);

        // Assert - Verify complete response structure
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();

        assertNotNull(poiResponse);
        assertNotNull(poiResponse.getSuggestions());
        assertFalse(poiResponse.getSuggestions().isEmpty());

        // Verify every suggestion is complete
        for (Suggestion poi : poiResponse.getSuggestions()) {
            assertNotNull(poi.getName(), "Name should not be null");
            assertNotNull(poi.getType(), "Type should not be null");
            assertNotNull(poi.getAddress(), "Address should not be null");
            assertNotNull(poi.getReason(), "Reason should not be null");

            assertFalse(poi.getName().trim().isEmpty(), "Name should not be empty");
            assertFalse(poi.getType().trim().isEmpty(), "Type should not be empty");
            assertFalse(poi.getAddress().trim().isEmpty(), "Address should not be empty");
            assertFalse(poi.getReason().trim().isEmpty(), "Reason should not be empty");
        }

        System.out.println("\n=== Complete Response Test ===");
        System.out.println("All " + poiResponse.getSuggestions().size() + " suggestions have complete data");
    }

    @Test
    void testGetPOIs_EndToEnd_MultipleConsecutiveRequests() throws Exception {
        // Test that multiple requests work correctly (no state issues)
        String[] queries = {"gas station", "restaurants", "coffee shops"};

        System.out.println("\n=== Multiple Consecutive Requests Test ===");

        for (String query : queries) {
            AIPOIsRequestBody request = new AIPOIsRequestBody();
            request.setOrigin("Charleston, SC");
            request.setDestination("Columbia, SC");
            request.setQuery(query);

            ResponseEntity<?> response = aiEndpoints.getPOIs(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            AIPOIsResponse poiResponse = (AIPOIsResponse) response.getBody();
            assertNotNull(poiResponse);
            assertFalse(poiResponse.getSuggestions().isEmpty());

            System.out.println("Query \"" + query + "\": " +
                             poiResponse.getSuggestions().size() + " results ✓");
        }
    }
}
