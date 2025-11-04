package edu.citadel.api;

import edu.citadel.main.RouteScoutAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AIEndpointsTest {

    private RouteScoutAgent routeScoutAgent;
    private AIEndpoints aiEndpoints;

    @BeforeEach
    void setUp() {
        routeScoutAgent = mock(RouteScoutAgent.class);
        aiEndpoints = new AIEndpoints(routeScoutAgent);
    }

    @Test
    void testGetSuggestions_returnsServiceAvailable() {
        // Act
        ResponseEntity<Object> response = aiEndpoints.getSuggestions();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("status"));
        assertEquals("Service available", body.get("status"));
    }

    @Test
    void testSuggest_withValidMessage_returnsSuccess() throws Exception {
        // Arrange
        String testMessage = "Find me a route from Charleston to Summerville";
        String expectedResponse = "{\"suggestions\": [\"Route 1\", \"Route 2\"]}";

        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage(testMessage);

        when(routeScoutAgent.getSuggestions(testMessage)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(routeScoutAgent, times(1)).getSuggestions(testMessage);
    }

    @Test
    void testSuggest_withNullMessage_returnsBadRequest() throws Exception {
        // Arrange
        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage(null);

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Message is required"));
        verify(routeScoutAgent, never()).getSuggestions(anyString());
    }

    @Test
    void testSuggest_withEmptyMessage_returnsBadRequest() throws Exception {
        // Arrange
        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage("");

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Message is required"));
        verify(routeScoutAgent, never()).getSuggestions(anyString());
    }

    @Test
    void testSuggest_withWhitespaceMessage_returnsBadRequest() throws Exception {
        // Arrange
        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage("   ");

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Message is required"));
        verify(routeScoutAgent, never()).getSuggestions(anyString());
    }

    @Test
    void testSuggest_withException_returnsInternalServerError() throws Exception {
        // Arrange
        String testMessage = "Find me a route";
        String errorMessage = "AI service unavailable";

        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage(testMessage);

        when(routeScoutAgent.getSuggestions(testMessage))
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Failed to get suggestions"));
        assertTrue(response.getBody().contains(errorMessage));
        verify(routeScoutAgent, times(1)).getSuggestions(testMessage);
    }

    @Test
    void testSuggest_withLongMessage_handlesCorrectly() throws Exception {
        // Arrange
        String longMessage = "A".repeat(1000);
        String expectedResponse = "{\"suggestions\": [\"Long route\"]}";

        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage(longMessage);

        when(routeScoutAgent.getSuggestions(longMessage)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(routeScoutAgent, times(1)).getSuggestions(longMessage);
    }

    @Test
    void testSuggest_withSpecialCharacters_handlesCorrectly() throws Exception {
        // Arrange
        String messageWithSpecialChars = "Find route with special chars: @#$%^&*()";
        String expectedResponse = "{\"suggestions\": [\"Special route\"]}";

        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        request.setMessage(messageWithSpecialChars);

        when(routeScoutAgent.getSuggestions(messageWithSpecialChars)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testConstructor() {
        // Test that constructor properly initializes dependencies
        AIEndpoints endpoints = new AIEndpoints(routeScoutAgent);
        assertNotNull(endpoints);
    }

    @Test
    void testSuggestionRequest_getterAndSetter() {
        // Arrange
        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();
        String testMessage = "Test message";

        // Act
        request.setMessage(testMessage);
        String result = request.getMessage();

        // Assert
        assertEquals(testMessage, result);
    }

    @Test
    void testSuggestionRequest_defaultConstructor() {
        // Act
        AIEndpoints.SuggestionRequest request = new AIEndpoints.SuggestionRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getMessage());
    }
}