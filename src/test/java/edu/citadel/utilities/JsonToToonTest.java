package edu.citadel.utilities;

import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.api.response.AISuggestionToonResponse;
import edu.citadel.dal.model.Suggestion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JsonToToonTest {

    @Test
    void testConvertJsonToToon_WithHashMap_ValidData() {
        // Arrange
        HashMap<String, Object> testMap = new HashMap<>();
        testMap.put("name", "Test Location");
        testMap.put("type", "Restaurant");
        testMap.put("rating", 4.5);

        // Act
        String result = JsonToToon.convertJsonToToon(testMap);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // TOON format should be different from plain JSON
        assertNotEquals("{\"name\":\"Test Location\",\"type\":\"Restaurant\",\"rating\":4.5}", result);
    }

    @Test
    void testConvertJsonToToon_WithHashMap_EmptyMap() {
        // Arrange
        HashMap<String, Object> emptyMap = new HashMap<>();

        // Act
        String result = JsonToToon.convertJsonToToon(emptyMap);

        // Assert
        assertNotNull(result);
        // Empty map can produce empty TOON string, which is valid
    }

    @Test
    void testConvertJsonToToon_WithHashMap_NestedData() {
        // Arrange
        HashMap<String, Object> nestedMap = new HashMap<>();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put("city", "Charleston");
        innerMap.put("state", "SC");
        nestedMap.put("location", innerMap);
        nestedMap.put("name", "Test Place");

        // Act
        String result = JsonToToon.convertJsonToToon(nestedMap);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithAISuggestionToonResponse_ValidData() {
        // Arrange
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(createMockSuggestion("Restaurant A", "dining", "123 Main St", "Great food"));
        suggestions.add(createMockSuggestion("Park B", "recreation", "456 Oak Ave", "Beautiful scenery"));

        AISuggestionToonResponse response = new AISuggestionToonResponse(suggestions, "nextToken123");

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithAISuggestionToonResponse_EmptySuggestions() {
        // Arrange
        ArrayList<Suggestion> emptySuggestions = new ArrayList<>();
        AISuggestionToonResponse response = new AISuggestionToonResponse(emptySuggestions, "token456");

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithAISuggestionToonResponse_NullNextPageToken() {
        // Arrange
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(createMockSuggestion("Museum", "culture", "789 Elm St", "Educational"));

        AISuggestionToonResponse response = new AISuggestionToonResponse(suggestions, null);

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithAIPOIsResponse_ValidData() {
        // Arrange
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(createMockSuggestion("Hotel A", "lodging", "321 Pine St", "Comfortable stay"));
        suggestions.add(createMockSuggestion("Cafe B", "dining", "654 Maple Dr", "Great coffee"));
        suggestions.add(createMockSuggestion("Shop C", "shopping", "987 Cedar Ln", "Unique items"));

        AIPOIsResponse response = new AIPOIsResponse(suggestions);

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithAIPOIsResponse_EmptySuggestions() {
        // Arrange
        ArrayList<Suggestion> emptySuggestions = new ArrayList<>();
        AIPOIsResponse response = new AIPOIsResponse(emptySuggestions);

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithAIPOIsResponse_SingleSuggestion() {
        // Arrange
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(createMockSuggestion("Library", "education", "111 Book St", "Quiet place"));

        AIPOIsResponse response = new AIPOIsResponse(suggestions);

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_WithHashMap_ComplexTypes() {
        // Arrange
        HashMap<String, Object> complexMap = new HashMap<>();
        complexMap.put("string", "value");
        complexMap.put("integer", 42);
        complexMap.put("double", 3.14);
        complexMap.put("boolean", true);

        ArrayList<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        complexMap.put("list", list);

        // Act
        String result = JsonToToon.convertJsonToToon(complexMap);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testConvertJsonToToon_MultipleConversions_ProducesConsistentResults() {
        // Arrange
        HashMap<String, Object> testMap = new HashMap<>();
        testMap.put("key", "value");

        // Act
        String result1 = JsonToToon.convertJsonToToon(testMap);
        String result2 = JsonToToon.convertJsonToToon(testMap);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
    }

    @Test
    void testConvertJsonToToon_DifferentResponseTypes_ProduceDifferentResults() {
        // Arrange
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(createMockSuggestion("Place", "type", "address", "reason"));

        AISuggestionToonResponse toonResponse = new AISuggestionToonResponse(suggestions, "token");
        AIPOIsResponse poiResponse = new AIPOIsResponse(suggestions);

        // Act
        String toonResult = JsonToToon.convertJsonToToon(toonResponse);
        String poiResult = JsonToToon.convertJsonToToon(poiResponse);

        // Assert
        assertNotNull(toonResult);
        assertNotNull(poiResult);
        // These should be different because one has nextPageToken and the other doesn't
        assertNotEquals(toonResult, poiResult);
    }

    @Test
    void testConvertJsonToToon_WithAISuggestionToonResponse_MultipleSuggestions() {
        // Arrange
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            suggestions.add(createMockSuggestion(
                "Place " + i,
                "type " + i,
                "address " + i,
                "reason " + i
            ));
        }

        AISuggestionToonResponse response = new AISuggestionToonResponse(suggestions, "largeToken");

        // Act
        String result = JsonToToon.convertJsonToToon(response);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Helper method to create a mock Suggestion for testing
     */
    private Suggestion createMockSuggestion(String name, String type, String address, String reason) {
        Suggestion suggestion = new Suggestion();
        suggestion.setName(name);
        suggestion.setType(type);
        suggestion.setAddress(address);
        suggestion.setReason(reason);
        return suggestion;
    }
}