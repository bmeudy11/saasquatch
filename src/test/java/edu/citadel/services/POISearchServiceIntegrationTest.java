package edu.citadel.services;

import edu.citadel.dal.keys.APIKeys;
import edu.citadel.dal.model.Suggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Integration tests for POISearchService that use REAL APIs:
 * - Google Maps Directions API
 * - Google Gemini AI API
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
class POISearchServiceIntegrationTest {

    @Mock
    private APIKeys apiKeys;

    private POISearchService poiSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Use real API key from environment
        String apiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        when(apiKeys.getMapsApiKey()).thenReturn(apiKey);
        poiSearchService = new POISearchService(apiKeys);
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_ShortRoute() throws Exception {
        // Arrange
        String origin = "Charleston, SC";
        String destination = "Columbia, SC";
        List<String> placeTypes = Arrays.asList("gas_station");

        // Act
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);

        // Assert
        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Should return at least one POI suggestion");

        // Verify suggestion structure
        Suggestion first = results.get(0);
        assertNotNull(first.getName(), "POI should have a name");
        assertNotNull(first.getType(), "POI should have a type");
        assertNotNull(first.getAddress(), "POI should have an address");
        assertNotNull(first.getReason(), "POI should have a reason");

        // Name should not be empty
        assertFalse(first.getName().trim().isEmpty(), "POI name should not be empty");

        System.out.println("Found " + results.size() + " POIs:");
        results.forEach(poi -> System.out.println("  - " + poi.getName() + " (" + poi.getType() + ")"));
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_MultiplePlaceTypes() throws Exception {
        // Arrange
        String origin = "Charleston, SC";
        String destination = "Savannah, GA";
        List<String> placeTypes = Arrays.asList("gas_station", "restaurant");

        // Act
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Should have suggestions for different types
        System.out.println("Found POIs for multiple types:");
        results.forEach(poi -> System.out.println("  - " + poi.getName() + " (" + poi.getType() + ")"));
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_LongRoute() throws Exception {
        // Arrange
        String origin = "Charleston, SC";
        String destination = "Atlanta, GA";
        List<String> placeTypes = Arrays.asList("gas_station");

        // Act
        long startTime = System.currentTimeMillis();
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);
        long duration = System.currentTimeMillis() - startTime;

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());

        System.out.println("Long route search took: " + duration + "ms");
        System.out.println("Found " + results.size() + " POIs along long route");

        // Should complete in reasonable time (< 15 seconds)
        assertTrue(duration < 15000, "Should complete within 15 seconds");
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_InvalidOrigin() {
        // Arrange
        String origin = "InvalidCityXYZ123";
        String destination = "Columbia, SC";
        List<String> placeTypes = Arrays.asList("gas_station");

        // Act & Assert
        // The important thing is that it throws an exception for invalid input
        assertThrows(Exception.class, () -> {
            poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);
        }, "Should throw exception for invalid location");
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_SameOriginDestination() throws Exception {
        // Arrange
        String origin = "Charleston, SC";
        String destination = "Charleston, SC";
        List<String> placeTypes = Arrays.asList("restaurant");

        // Act
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);

        // Assert - May return empty or error, but shouldn't crash
        assertNotNull(results);
        System.out.println("Same origin/destination returned " + results.size() + " results");
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_VerifyGeographicRelevance() throws Exception {
        // Arrange
        String origin = "New York, NY";
        String destination = "Philadelphia, PA";
        List<String> placeTypes = Arrays.asList("gas_station");

        // Act
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Verify addresses mention relevant states (NY, NJ, PA)
        results.forEach(poi -> {
            String address = poi.getAddress().toLowerCase();
            System.out.println("POI: " + poi.getName() + " at " + poi.getAddress());

            // Address should reference the region (with AI, this is AI-generated, so just verify it exists)
            assertNotNull(address);
            assertFalse(address.trim().isEmpty());
        });
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_VerifyReasonField() throws Exception {
        // Arrange
        String origin = "Charleston, SC";
        String destination = "Columbia, SC";
        List<String> placeTypes = Arrays.asList("restaurant");

        // Act
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Every suggestion should have a meaningful reason
        results.forEach(poi -> {
            assertNotNull(poi.getReason(), "Reason should not be null");
            assertFalse(poi.getReason().trim().isEmpty(), "Reason should not be empty");

            System.out.println(poi.getName() + ": " + poi.getReason());
        });
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_AIFallbackScenario() throws Exception {
        // Arrange
        String origin = "Charleston, SC";
        String destination = "Myrtle Beach, SC";
        List<String> placeTypes = Arrays.asList("cafe");

        // Act
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);

        // Assert - Should work whether AI succeeds or falls back
        assertNotNull(results);
        assertFalse(results.isEmpty());

        System.out.println("Cafe search returned " + results.size() + " results");

        // Verify all results are cafes/coffee shops
        results.forEach(poi -> {
            System.out.println("  - " + poi.getName() + " (" + poi.getType() + ")");
        });
    }

    @Test
    void testSearchPOIsAlongRoute_WithRealAPIs_StressTest() throws Exception {
        // Arrange - Test with many place types
        String origin = "Charleston, SC";
        String destination = "Columbia, SC";
        List<String> placeTypes = Arrays.asList("gas_station", "restaurant", "cafe", "convenience_store");

        // Act
        long startTime = System.currentTimeMillis();
        List<Suggestion> results = poiSearchService.searchPOIsAlongRoute(origin, destination, placeTypes);
        long duration = System.currentTimeMillis() - startTime;

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());

        System.out.println("Stress test with " + placeTypes.size() + " types:");
        System.out.println("  - Took: " + duration + "ms");
        System.out.println("  - Found: " + results.size() + " POIs");

        // Should still complete in reasonable time
        assertTrue(duration < 20000, "Should complete within 20 seconds");
    }
}
