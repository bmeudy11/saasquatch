package edu.citadel.api;

import edu.citadel.api.request.AISuggestionRequestBody;
import edu.citadel.api.response.AISuggestionResponse;
import edu.citadel.dal.model.Suggestion;
import edu.citadel.main.RouteScoutAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
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
        AISuggestionResponse expectedResponse = new AISuggestionResponse();
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        Suggestion suggestion = new Suggestion();
        Suggestion suggestion2 = new Suggestion();
        suggestion.setName("Route 1");
        suggestion2.setName("Route 2");
        suggestions.add(suggestion);
        suggestions.add(suggestion2);
        expectedResponse.setSuggestions(suggestions);


        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage(testMessage);

        when(routeScoutAgent.getSuggestions(testMessage)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(routeScoutAgent, times(1)).getSuggestions(testMessage);
    }

    @Test
    void testSuggest_withNullMessage_returnsBadRequest() throws Exception {
        // Arrange
        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage(null);

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(true);
        verify(routeScoutAgent, never()).getSuggestions(anyString());
    }

    @Test
    void testSuggest_withEmptyMessage_returnsBadRequest() throws Exception {
        // Arrange
        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage("");

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Message is required"));
        verify(routeScoutAgent, never()).getSuggestions(anyString());
    }

    @Test
    void testSuggest_withWhitespaceMessage_returnsBadRequest() throws Exception {
        // Arrange
        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage("   ");

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Message is required"));
        verify(routeScoutAgent, never()).getSuggestions(anyString());
    }

    @Test
    void testSuggest_withException_returnsInternalServerError() throws Exception {
        // Arrange
        String testMessage = "Find me a route";
        String errorMessage = "AI service unavailable";

        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage(testMessage);

        when(routeScoutAgent.getSuggestions(testMessage))
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Failed to get suggestions"));
        assertTrue(response.getBody().toString().contains(errorMessage));
        verify(routeScoutAgent, times(1)).getSuggestions(testMessage);
    }

    @Test
    void testSuggest_withLongMessage_handlesCorrectly() throws Exception {
        // Arrange
        String longMessage = "A".repeat(1000);

        AISuggestionResponse expectedResponse = new AISuggestionResponse();
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        Suggestion suggestion = new Suggestion();
        suggestion.setName("Long route");
        suggestions.add(suggestion);
        expectedResponse.setSuggestions(suggestions);

        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage(longMessage);

        when(routeScoutAgent.getSuggestions(longMessage)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

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
        AISuggestionResponse expectedResponse = new AISuggestionResponse();
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        Suggestion suggestion = new Suggestion();
        suggestion.setName("Special route");
        suggestions.add(suggestion);
        expectedResponse.setSuggestions(suggestions);

        AISuggestionRequestBody request = new AISuggestionRequestBody();
        request.setMessage(messageWithSpecialChars);

        when(routeScoutAgent.getSuggestions(messageWithSpecialChars)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = aiEndpoints.suggest(request);

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
        AISuggestionRequestBody request = new AISuggestionRequestBody();
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
        AISuggestionRequestBody request = new AISuggestionRequestBody();

        // Assert
        assertNotNull(request);
        assertNull(request.getMessage());
    }
}